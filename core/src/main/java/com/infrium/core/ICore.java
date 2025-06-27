package com.infrium.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.PaperCommandManager;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import com.infrium.api.InfriumProvider;
import com.infrium.api.configuration.InfriumConfiguration;
import com.infrium.api.hive.enums.CloudChannels;
import com.infrium.api.hive.pubsub.hive.RedisHiveMessage;
import com.infrium.api.hive.pubsub.hive.RedisHiveShutdown;
import com.infrium.api.hive.pubsub.hive.RedisHiveUpdate;
import com.infrium.api.util.Constants;
import com.infrium.core.commands.ServerCommand;
import com.infrium.core.configuration.CoreConfiguration;
import com.infrium.core.configuration.YamlConfigurationContainer;
import com.infrium.core.gui.extra.ServerSelectorGUI;
import com.infrium.core.helpers.InfriumScoreBoard;
import com.infrium.core.listener.PlayerListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

@Getter
public class ICore extends JavaPlugin {

    private static final RedisHiveUpdate REDIS_CLOUD_SERVER_UPDATE = new RedisHiveUpdate();

    @Getter
    @Setter
    private static final RedisHiveMessage REDIS_HIVE_MESSAGE = new RedisHiveMessage();
    @Getter
    private static com.infrium.core.ICore instance;
    private InfriumProvider<Player> infriumProvider;
    private BukkitCommandManager commandManager;

    private ServerSelectorGUI serverSelectorGUI;


    @Getter private YamlConfigurationContainer configuration;
    private volatile long lastUpdate = 0;
    private String serverName;

    @Override
    public void onEnable() {
        com.infrium.core.ICore.instance = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        initServerName();

        File path = new File(getDataFolder(), "core.yml");
        try {
            if (!path.exists()) {
                getDataFolder().mkdirs();
                path.createNewFile();
            }
            YamlConfiguration yamlConfig = new YamlConfiguration();
            yamlConfig.load(path);

            // Set defaults if not present
            for (InfriumConfiguration config : InfriumConfiguration.values()) {
                if (!yamlConfig.contains(config.getKey())) {
                    yamlConfig.set(config.getKey(), config.getDefaultValue());
                }
            }
            for (CoreConfiguration config : CoreConfiguration.values()) {
                if (!yamlConfig.contains(config.getKey())) {
                    yamlConfig.set(config.getKey(), config.getDefaultValue());
                }
            }

            // Save the configuration
            yamlConfig.save(path);

            this.configuration = new YamlConfigurationContainer(yamlConfig, path);
            infriumProvider = new BukkitInfriumProvider(this.configuration);
        } catch (Exception e) {
            throw new RuntimeException(e); // re throw exception so the plugin will be disabled
        }

        Bukkit.getOnlinePlayers()
                .forEach(
                        (player) -> {
                            InfriumScoreBoard.createScore(player); // create scoreboard for the players online
                            setupPrefix(player);
                        });

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents((Listener) infriumProvider, this);
        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ServerCommand());

        this.serverSelectorGUI = new ServerSelectorGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Shard Selector"),
                this);

        this.getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(
                        this,
                        () -> {
                            if (System.currentTimeMillis() - lastUpdate > 60000) { // send update every minute
                                sendUpdate(false);
                            }
                        },
                        0,
                        20L); // send update every minute

        this.getInfriumProvider()
                .getInfriumDB()
                .getPubSubConnectionReceiver()
                .sync()
                .subscribe(CloudChannels.SERVER_SHUTDOWN.getChannel());
        this.getInfriumProvider()
                .getInfriumDB()
                .getPubSubConnectionReceiver()
                .addListener(
                        new RedisPubSubAdapter<>() {
                            @Override
                            public void message(String channel, String message) {
                                Bukkit.getScheduler()
                                        .runTask(
                                                com.infrium.core.ICore.this,
                                                () -> {
                                                    if (channel.equalsIgnoreCase(
                                                            CloudChannels.SERVER_SHUTDOWN.getChannel())) {
                                                        getLogger().warning("Received shutdown message from hive");
                                                        var msg =
                                                                Constants.get()
                                                                        .getGson()
                                                                        .fromJson(message, RedisHiveShutdown.class);
                                                        if (msg.getServerName().equals(getServerName())) { // is this server?
                                                            Bukkit.shutdown();
                                                        }
                                                    } else if (channel.equalsIgnoreCase(CloudChannels.SYNC.getChannel())) {
                                                        Bukkit.getScheduler()
                                                                .runTaskLater(
                                                                        com.infrium.core.ICore.this,
                                                                        () -> {
                                                                            sendUpdate(true);
                                                                        },
                                                                        20L * 5L);
                                                    }
                                                });
                            }
                        });
    }

    private void initServerName() {
        var name = System.getProperty("server_name");
        if (name == null) {
            getLogger()
                    .severe(
                            "SERVER_NAME environment variable not set! Getting it through config (this is not recommended)");
            try {
                Properties properties = new Properties();
                @Cleanup var fr = new FileReader("infriumserver.properties");
                properties.load(fr);
                serverName = properties.getProperty("serverName", "UNKNOWN-1");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.shutdown();
            }
        } else {
            this.serverName = name;
        }
    }

    @Override
    public void onDisable() {
        InfriumScoreBoard.flush(); // flush scoreboards
        infriumProvider.shutdown();
    }

    public void sendUpdate(boolean forced) {
        if (System.currentTimeMillis() - lastUpdate < 50
                && !forced) { // 100 ms of delay between updates
            return;
        }
        lastUpdate = System.currentTimeMillis();
        Bukkit.getScheduler()
                .runTaskAsynchronously(
                        this,
                        () -> {
                            String serverName0 = getServerName();

                            int onlinePlayers = Bukkit.getOnlinePlayers().size();
                            double tps = Bukkit.getTPS()[0];
                            long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                            long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                            long usedMemory = totalMemory - freeMemory;

                            REDIS_CLOUD_SERVER_UPDATE.setServerName(serverName0);
                            REDIS_CLOUD_SERVER_UPDATE.setOnlinePlayers(onlinePlayers);
                            REDIS_CLOUD_SERVER_UPDATE.setTps(tps);
                            REDIS_CLOUD_SERVER_UPDATE.setRamUsage(usedMemory);
                            REDIS_CLOUD_SERVER_UPDATE.setMotd(getMotd());

                            // TODO: depcrecate pubusb update messages
                            infriumProvider
                                    .getInfriumDB()
                                    .publishJson(CloudChannels.SERVER_UPDATE.getChannel(), REDIS_CLOUD_SERVER_UPDATE);
                            // TODO: manual updates
                            infriumProvider
                                    .getRepository()
                                    .getInfriumDB()
                                    .getRedisConnection()
                                    .async()
                                    .set(serverName0, Constants.get().getGson().toJson(REDIS_CLOUD_SERVER_UPDATE))
                                    .thenAccept(
                                            aVoid -> {
                                                if (aVoid != null) { // expiration is configured once the key has been
                                                    // successfully inserted
                                                    infriumProvider
                                                            .getRepository()
                                                            .getInfriumDB()
                                                            .getRedisConnection()
                                                            .async()
                                                            .expire(serverName0, 7L); // 7 seconds in case of delayed update
                                                }
                                            });
                        });
    }

    public final void setupPrefix(Player player) {
        infriumProvider
                .getInfriumPlayerAsync(player)
                .thenAccept(
                        infriumPlayer -> {
                            if (infriumPlayer.isPresent() && player.isOnline()) {
                                var prefix = infriumPlayer.get().getPlayerData().getRank().getPrefix();
                                if (prefix.length() > 0) {
                                    prefix = prefix + " ";
                                }
                                infriumPlayer
                                        .get()
                                        .getPlayerObject()
                                        .playerListName(
                                                MiniMessage.miniMessage().deserialize(prefix + "<white>" + infriumPlayer.get().getUsername()));
                            }
                        });
    }

    public String getMotd() {
        return Bukkit.getServer().motd().toString();
    }

    public static void info(String message) {
        com.infrium.core.ICore.getInstance().getLogger().info("[" + (Bukkit.isPrimaryThread() ? "-" : "+") + "] " + message);
    }

    public static void warning(String message) {
        com.infrium.core.ICore.getInstance().getLogger().warning("[X] " + "[" + (Bukkit.isPrimaryThread() ? "-" : "+") + "] " + message);
    }

}