package com.infrium.proxy;


import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import lombok.SneakyThrows;
import com.infrium.api.InfriumProvider;
import com.infrium.api.configuration.InfriumConfiguration;
import com.infrium.api.hive.ServerRepository;
import com.infrium.api.hive.queue.QueueRepository;
import com.infrium.proxy.commands.HubCommand;
import com.infrium.proxy.commands.PunishmentCommand;
import com.infrium.proxy.commands.QueueCommand;
import com.infrium.proxy.configuration.TomlConfigurationContainer;
import com.infrium.proxy.handler.QueueLimboHandler;
import com.infrium.proxy.listener.ServerListener;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Plugin(id = "proxy", name = "Proxy", version = "1.0",dependencies = {
        @Dependency(id = "limboapi")
})@Getter
public class Proxy {
    private static Proxy instance;

    public static final char HEAVY_VERTICAL = '\u2503';

    public static final Function<String, Component> serialize =
            message -> LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private LimboFactory limboFactory;
    private final Map<String, QueueLimboHandler> limboPlayers = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, RegisteredServer> queuedJoin = new ConcurrentHashMap<>();
    private InfriumProvider<Player> infriumProvider;
    private ServerRepository serverRepository;
    private QueueRepository queueRepository;
    private Limbo queueServer;



    @Inject
    public Proxy(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        Proxy.instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = folder;
        logger.info("Hello there! I made my first plugin with Velocity.");
        this.limboFactory =
                (LimboFactory)
                        this.server
                                .getPluginManager()
                                .getPlugin("limboapi")
                                .flatMap(PluginContainer::getInstance)
                                .orElseThrow();
    }

    @SneakyThrows
    private Toml loadConfig(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return new Toml().read(file);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        logger.info("Proxy initialized!");
        this.infriumProvider =
                new ProxyInfriumProvider(
                        new TomlConfigurationContainer(
                                loadConfig(new File(dataDirectory.toFile(), "InfriumAPI.toml"))));

        server.getEventManager().register(this, this.infriumProvider);
        server.getEventManager().register(this, new ServerListener());
        server.getChannelRegistrar().register(new LegacyChannelIdentifier("Bungeecord"));
        this.serverRepository =
                new ProxyServerRepository(
                        InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
        this.queueRepository = new ProxyQueueRepository(this.serverRepository);

        //remove limbo from servers after normal servers have been added
//        Proxy.get().getServer().getAllServers().stream().allMatch(server -> {
//            System.out.println(server.getServerInfo().getName());
//             if (server.getServerInfo().getName().contains("limbo")) {
//                this.server.unregisterServer(server.getServerInfo());
//            }
//            return true;
//        });
        VirtualWorld queueWorld =
                this.limboFactory.createVirtualWorld(Dimension.THE_END, 0, 0, 0, 90f, 90f);
        this.queueServer = this.limboFactory.createLimbo(queueWorld);
        registerCommand(new PunishmentCommand(), "punish", "mute", "tempban", "tempmute", "kick");
        registerCommand(new QueueCommand(), "queue", "join");
        registerCommand(new HubCommand(), "hub");
        System.out.println("Proxy initialized");
        for (var server : this.server.getAllServers()) {
            System.out.println(server.getServerInfo().getName());
        }
    }

    public void delistLimbo(){


    }

    public static Proxy get() {
        return instance;
    }

    public void registerQueueLimbo(String username, QueueLimboHandler queueLimboHandler) {
        System.out.println("regstered: " + username);

        System.out.println("still: " + queueLimboHandler.getPlayer().getProxyPlayer().getUsername());
        if (limboPlayers.containsKey(username)) {
            limboPlayers.replace(username, queueLimboHandler);
            System.out.println("as replacement");
        } else {
            limboPlayers.put(username, queueLimboHandler);
            System.out.println("as put");
        }
        System.out.println("now:" + limboPlayers.keySet());
    }

    public Optional<QueueLimboHandler> getQueueLimboHandler(String username) {
        return Optional.ofNullable(limboPlayers.get(username));
    }

    public void unregisterQueueLimbo(String username) {
        limboPlayers.remove(username);
    }

    public static InfriumProvider<Player> getInfriumProvider() {
        return instance.infriumProvider;
    }

    public static QueueRepository getQueueRepository() {
        return instance.queueRepository;
    }

    private void registerCommand(SimpleCommand command, String name, String... aliases) {
        this.getServer().getCommandManager().register(name, command, aliases);
    }

}
