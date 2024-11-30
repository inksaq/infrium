package ltd.bui.infrium.core.sponge.events;

import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class OnSyncEvent extends AbstractEvent {

    private final Cause cause;

    public OnSyncEvent(Cause cause) {
        this.cause = cause;
    }

    @Override
    public Cause cause() {
        return cause;
    }
}