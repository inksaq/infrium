package ltd.bui.infrium.api.player;

import lombok.Data;
import lombok.NonNull;
import ltd.bui.infrium.api.data.InfriumPlayerData;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@Data
public abstract class AbstractInfriumPlayer<T> {

    protected final T playerObject;

    private InfriumPlayerData playerData;

    public AbstractInfriumPlayer(T playerObject) {
        this.playerObject = playerObject;
        this.playerData = new InfriumPlayerData(this.getUniqueId(), this.getUsername());
    }

    public abstract void sendMessage(@NonNull Component component);

    public abstract void sendActionBar(@NonNull Component component);

    public abstract void sendTitle(
            @NonNull Component title,
            @NonNull Component subtitle,
            @NonNull long fadeIn,
            @NonNull long stay,
            @NonNull long fadeOut);

    public abstract void disconnect(@NonNull Component component);

    public abstract String getUsername();

    public abstract UUID getUniqueId();

    public abstract boolean isOnline();

    public abstract Optional<InetSocketAddress> getAddress();
}
