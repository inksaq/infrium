package ltd.bui.infrium.core.sponge.events;

import ltd.bui.infrium.api.hive.servers.Server;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class OnServerUpdateEvent extends AbstractEvent {

    private final Server server;
    private final Cause cause;

    public OnServerUpdateEvent(Server server, Cause cause) {
        this.server = server;
        this.cause = cause;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public Cause cause() {
        return cause;
    }
}

