package com.infrium.core;//import com.google.inject.Inject;
//import org.spongepowered.api.Sponge;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.Listener;
//import org.spongepowered.api.event.network.ServerSideConnectionEvent;
//import org.spongepowered.configurate.ConfigurationNode;
//import org.spongepowered.plugin.PluginContainer;


public class SpongeInfriumProvider /*extends InfriumProvider<Player> */{

//    @Inject
//    private PluginContainer container;
//
//    public SpongeInfriumProvider(ConfigurationContainer<ConfigurationNode> configurationContainer) throws IOException {
//        super(configurationContainer);
//    }
//
//    @Override
//    public List<Player> getOnlinePlayers() {
//        return new ArrayList<>(Sponge.server().onlinePlayers());
//    }
//
//    @Override
//    public AbstractInfriumPlayer<Player> craftInfriumPlayer(@NonNull Player playerObject) {
//        return new SpongeInfriumPlayer(playerObject);
//    }
//
//    @Listener
//    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
//        Sponge.asyncScheduler().submit(() -> onJoin(event.player()));
//        // You'll need to implement setupPrefix method in your Sponge main class
//        ((ICoreSponge) container).setupPrefix(event.player());
//    }
//
//    @Listener
//    public void onPlayerQuit(ServerSideConnectionEvent.Disconnect event) {
//        Sponge.asyncScheduler().submit(() -> onQuit(event.player()));
//    }
//
//    @Override
//    public @NonNull ServerRepository serverRepositoryBuilder() {
//        return new SpongeServerRepository(
//                InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
//    }
//
//    private final class SpongeServerRepository extends ServerRepository {
//
//        public SpongeServerRepository(String redisUri, String mongoUri) {
//            super(redisUri, mongoUri);
//        }
//
//        private void postEvent(Object event) {
//            Sponge.eventManager().post(event);
//        }
//
//        @Override
//        public void onServerAdd(@NonNull Server server) {
//            postEvent(new OnServerAddEvent(server));
//        }
//
//        @Override
//        public void onServerDelete(@NonNull Server server) {
//            postEvent(new OnServerDeleteEvent(server));
//        }
//
//        @Override
//        public void onServerUpdate(@NonNull Server server) {
//            postEvent(new OnServerUpdateEvent(server));
//        }
//
//        @Override
//        public void onSync() {
//            postEvent(new OnSyncEvent());
//        }
//    }
}