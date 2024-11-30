package ltd.bui.infrium.core.sponge;

import com.google.inject.Inject;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.Getter;
import ltd.bui.infrium.api.InfriumProvider;
import ltd.bui.infrium.api.configuration.InfriumConfiguration;
import ltd.bui.infrium.api.hive.enums.CloudChannels;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveMessage;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveShutdown;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveUpdate;
import ltd.bui.infrium.api.util.Constants;
import ltd.bui.infrium.core.sponge.configuration.CoreConfiguration;
import ltd.bui.infrium.core.sponge.configuration.SpongeConfigurationContainer;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Plugin("infrium-core-sponge")
public class InfriumCoreSponge {

    @Getter
    private static InfriumCoreSponge instance;

    private static final RedisHiveUpdate REDIS_CLOUD_SERVER_UPDATE = new RedisHiveUpdate();

    private static final RedisHiveMessage REDIS_HIVE_MESSAGE = new RedisHiveMessage();

    @Inject
    private PluginContainer container;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private InfriumProvider<Player> infriumProvider;
    private CommentedConfigurationNode config;
    private volatile long lastUpdate = 0;
    private String serverName;
    @Getter
    private ScheduledTask updateTask;

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {
        instance = this;
        initServerName();
        loadConfig();
        initInfriumProvider();
        setupRedisListeners();
        scheduleUpdates();

        // TODO: Setup other components like PlayerListener, ServerSelectorGUI, etc.
    }


    public CommentedConfigurationNode getConfiguration() {
        return config;
    }

    private void initServerName() {
        String name = System.getProperty("server_name");
        if (name == null) {
            container.logger().error("SERVER_NAME environment variable not set! Getting it through config (this is not recommended)");
            try {
                Properties properties = new Properties();
                properties.load(getClass().getResourceAsStream("/infriumserver.properties"));
                serverName = properties.getProperty("serverName", "UNKNOWN-1");
            } catch (Exception e) {
                e.printStackTrace();
                Sponge.server().shutdown();
            }
        } else {
            this.serverName = name;
        }
    }

    private void loadConfig() {
        Path configPath = configDir.resolve("core.conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(configPath)
                .build();
        try {
            config = loader.load();
            // Set defaults if not present
            for (InfriumConfiguration configItem : InfriumConfiguration.values()) {
                if (config.node(configItem.getKey()).virtual()) {
                    config.node(configItem.getKey()).set(configItem.getDefaultValue());
                }
            }
            for (CoreConfiguration configItem : CoreConfiguration.values()) {
                if (config.node(configItem.getKey()).virtual()) {
                    config.node(configItem.getKey()).set(configItem.getDefaultValue());
                }
            }
            loader.save(config);
        } catch (IOException e) {
            container.logger().error("Failed to load config", e);
        }
    }

    private void initInfriumProvider() {
        try {
            Path configPath = configDir.resolve("core.conf");
            File configFile = configPath.toFile();

            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .path(configPath)
                    .build();

            CommentedConfigurationNode config;
            if (!configFile.exists()) {
                config = loader.createNode();
                // Set default values here if needed
                for (CoreConfiguration configItem : CoreConfiguration.values()) {
                    config.node(configItem.getKey().split("\\.")).set(configItem.getDefaultValue());
                }
                loader.save(config);
            } else {
                config = loader.load();
            }

            SpongeConfigurationContainer configContainer = new SpongeConfigurationContainer(config, configFile);
            infriumProvider = new SpongeInfriumProvider(configContainer);
        } catch (IOException e) {
            container.logger().error("Failed to initialize InfriumProvider", e);
        }
    }

    private void setupRedisListeners() {
        infriumProvider.getInfriumDB().getPubSubConnectionReceiver().sync()
                .subscribe(CloudChannels.SERVER_SHUTDOWN.getChannel());
        infriumProvider.getInfriumDB().getPubSubConnectionReceiver().addListener(
                new RedisPubSubAdapter<>() {
                    @Override
                    public void message(String channel, String message) {
                        Task task = Task.builder()
                                .execute(() -> {
                                    if (channel.equalsIgnoreCase(CloudChannels.SERVER_SHUTDOWN.getChannel())) {
                                        container.logger().warn("Received shutdown message from hive");
                                        var msg = Constants.get().getGson().fromJson(message, RedisHiveShutdown.class);
                                        if (msg.getServerName().equals(getServerName())) {
                                            Sponge.server().shutdown();
                                        }
                                    } else if (channel.equalsIgnoreCase(CloudChannels.SYNC.getChannel())) {
                                        Task delayedTask = Task.builder()
                                                .execute(() -> sendUpdate(true))
                                                .delay(5, TimeUnit.SECONDS)
                                                .plugin(container)
                                                .build();
                                        Sponge.server().scheduler().submit(delayedTask);
                                    }
                                })
                                .plugin(container)
                                .build();
                        Sponge.server().scheduler().submit(task);
                    }
                });
    }

    private void scheduleUpdates() {
        updateTask = (ScheduledTask) Task.builder()
                .execute(() -> {
                    if (System.currentTimeMillis() - lastUpdate > 60000) {
                        sendUpdate(false);
                    }
                })
                .interval(1, TimeUnit.MINUTES)
                .plugin(container)
                .build();

        Sponge.server().scheduler().submit((Task) updateTask);
    }


    public void sendUpdate(boolean forced) {
        if (System.currentTimeMillis() - lastUpdate < 50 && !forced) {
            return;
        }
        lastUpdate = System.currentTimeMillis();

        Task.builder()
                .execute(() -> {
                    String serverName0 = getServerName();

                    int onlinePlayers = Sponge.server().onlinePlayers().size();
                    double tps = Sponge.server().ticksPerSecond();
                    long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                    long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                    long usedMemory = totalMemory - freeMemory;

                    REDIS_CLOUD_SERVER_UPDATE.setServerName(serverName0);
                    REDIS_CLOUD_SERVER_UPDATE.setOnlinePlayers(onlinePlayers);
                    REDIS_CLOUD_SERVER_UPDATE.setTps(tps);
                    REDIS_CLOUD_SERVER_UPDATE.setRamUsage(usedMemory);
                    REDIS_CLOUD_SERVER_UPDATE.setMotd(getMotd());

                    infriumProvider.getInfriumDB().publishJson(CloudChannels.SERVER_UPDATE.getChannel(), REDIS_CLOUD_SERVER_UPDATE);
                    infriumProvider.getRepository().getInfriumDB().getRedisConnection().async()
                            .set(serverName0, Constants.get().getGson().toJson(REDIS_CLOUD_SERVER_UPDATE))
                            .thenAccept(aVoid -> {
                                if (aVoid != null) {
                                    infriumProvider.getRepository().getInfriumDB().getRedisConnection().async()
                                            .expire(serverName0, 7L);
                                }
                            });
                })
                .plugin(container)
                .build();

        Sponge.server().scheduler().submit(Task.builder().execute(() -> {
            // Your task code here
        }).plugin(container).build());
    }

    public String getMotd() {
        return Sponge.server().motd().toString();
    }

    public String getServerName() {
        return serverName;
    }

    public void onDisable() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }


//    // Utility methods
//    public static void info(String message) {
//        Sponge.server().logger().info("[" + (Sponge.server().onMainThread() ? "-" : "+") + "] " + message);
//    }
//
//    public static void warning(String message) {
//        Sponge.server().logger().warn("[X] " + "[" + (Sponge.server().onMainThread() ? "-" : "+") + "] " + message);
//    }
}