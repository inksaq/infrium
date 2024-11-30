package ltd.bui.infrium.core.sponge;

import com.google.inject.Inject;
import ltd.bui.infrium.api.InfriumProvider;
import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import ltd.bui.infrium.api.configuration.InfriumConfiguration;
import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.core.sponge.events.OnServerAddEvent;
import ltd.bui.infrium.core.sponge.events.OnServerDeleteEvent;
import ltd.bui.infrium.core.sponge.events.OnServerUpdateEvent;
import ltd.bui.infrium.core.sponge.events.OnSyncEvent;
import ltd.bui.infrium.core.sponge.player.SpongeInfriumPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.plugin.PluginContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpongeInfriumProvider extends InfriumProvider<Player> {

    @Inject
    private PluginContainer container;

    public SpongeInfriumProvider(ConfigurationContainer<CommentedConfigurationNode> configurationContainer) throws IOException {
        super(configurationContainer);
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(Sponge.server().onlinePlayers());
    }

    @Override
    public AbstractInfriumPlayer<Player> craftInfriumPlayer(Player playerObject) {
        return new SpongeInfriumPlayer(playerObject);
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        Task task = Task.builder()
                .execute(() -> onJoin(event.player()))
                .plugin(container)
                .build();
        Sponge.server().scheduler().submit(task);
    }

    @Listener
    public void onPlayerQuit(ServerSideConnectionEvent.Disconnect event) {
        Task task = Task.builder()
                .execute(() -> onQuit(event.player()))
                .plugin(container)
                .build();
        Sponge.server().scheduler().submit(task);
    }

    @Override
    public ServerRepository serverRepositoryBuilder() {
        return new SpongeServerRepository(
                InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
    }

    private final class SpongeServerRepository extends ServerRepository {

        public SpongeServerRepository(String redisUri, String mongoUri) {
            super(redisUri, mongoUri);
        }

        private void postEvent(Object event) {
            Task task = Task.builder()
                    .execute(() -> Sponge.eventManager().post((Event) event))
                    .plugin(container)
                    .build();
            Sponge.server().scheduler().submit(task);
        }

        @Override
        public void onServerAdd(Server server) {
            postEvent(new OnServerAddEvent(server, Sponge.server().causeStackManager().currentCause()));
        }

        @Override
        public void onServerDelete(Server server) {
            postEvent(new OnServerDeleteEvent(server, Sponge.server().causeStackManager().currentCause()));
        }

        @Override
        public void onServerUpdate(Server server) {
            postEvent(new OnServerUpdateEvent(server, Sponge.server().causeStackManager().currentCause()));
        }

        @Override
        public void onSync() {
            postEvent(new OnSyncEvent(Sponge.server().causeStackManager().currentCause()));
        }
    }
}