import lombok.NonNull;
import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import ltd.bui.infrium.api.configuration.InfriumConfiguration;
import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.core.BukkitInfriumProvider;
import ltd.bui.infrium.core.InfriumCore;
import ltd.bui.infrium.core.events.OnServerAddEvent;
import ltd.bui.infrium.core.events.OnServerDeleteEvent;
import ltd.bui.infrium.core.events.OnServerUpdateEvent;
import ltd.bui.infrium.core.events.OnSyncEvent;
import ltd.bui.infrium.core.player.BukkitInfriumPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpongeInfriumProvider extends InfriumProvider<Player> {

    @Inject
    private PluginContainer container;

    public SpongeInfriumProvider(ConfigurationContainer<ConfigurationNode> configurationContainer) throws IOException {
        super(configurationContainer);
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(Sponge.server().onlinePlayers());
    }

    @Override
    public AbstractInfriumPlayer<Player> craftInfriumPlayer(@NonNull Player playerObject) {
        return new SpongeInfriumPlayer(playerObject);
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        Sponge.asyncScheduler().submit(() -> onJoin(event.player()));
        // You'll need to implement setupPrefix method in your Sponge main class
        ((InfriumCoreSponge) container).setupPrefix(event.player());
    }

    @Listener
    public void onPlayerQuit(ServerSideConnectionEvent.Disconnect event) {
        Sponge.asyncScheduler().submit(() -> onQuit(event.player()));
    }

    @Override
    public @NonNull ServerRepository serverRepositoryBuilder() {
        return new SpongeServerRepository(
                InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
    }

    private final class SpongeServerRepository extends ServerRepository {

        public SpongeServerRepository(String redisUri, String mongoUri) {
            super(redisUri, mongoUri);
        }

        private void postEvent(Object event) {
            Sponge.eventManager().post(event);
        }

        @Override
        public void onServerAdd(@NonNull Server server) {
            postEvent(new OnServerAddEvent(server));
        }

        @Override
        public void onServerDelete(@NonNull Server server) {
            postEvent(new OnServerDeleteEvent(server));
        }

        @Override
        public void onServerUpdate(@NonNull Server server) {
            postEvent(new OnServerUpdateEvent(server));
        }

        @Override
        public void onSync() {
            postEvent(new OnSyncEvent());
        }
    }
}