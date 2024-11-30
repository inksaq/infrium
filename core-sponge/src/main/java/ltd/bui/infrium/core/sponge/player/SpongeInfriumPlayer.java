package ltd.bui.infrium.core.sponge.player;

import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.server.ServerLocation;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class SpongeInfriumPlayer extends AbstractInfriumPlayer<Player> {

    public SpongeInfriumPlayer(Player playerObject) {
        super(playerObject);
    }

    @Override
    public void sendMessage(@NonNull Component component) {
        this.playerObject.sendMessage(component);
    }

    @Override
    public void sendActionBar(@NonNull Component component) {
        this.playerObject.sendActionBar(component);
    }

    @Override
    public void sendTitle(
            @NonNull Component title,
            @NonNull Component subtitle,
            @NonNull long fadeIn,
            @NonNull long stay,
            @NonNull long fadeOut) {
        Title.Times times = Title.Times.of(
                Duration.ofMillis(fadeIn * 50),
                Duration.ofMillis(stay * 50),
                Duration.ofMillis(fadeOut * 50)
        );
        Title titleToSend = Title.title(title, subtitle, times);
        this.playerObject.showTitle(titleToSend);
    }

    @Override
    public void disconnect(@NonNull Component component) {
        if (this.playerObject instanceof ServerPlayer) {
            ((ServerPlayer) this.playerObject).kick(component);
        }
    }

    @Override
    public String getUsername() {
        return this.playerObject.name();
    }

    @Override
    public UUID getUniqueId() {
        return this.playerObject.uniqueId();
    }

    @Override
    public boolean isOnline() {
        return Sponge.server().player(this.playerObject.uniqueId()).isPresent();
    }

    @Override
    public Optional<InetSocketAddress> getAddress() {
        if (this.playerObject instanceof ServerPlayer) {
            return Optional.ofNullable(((ServerPlayer) this.playerObject).connection().address());
        }
        return Optional.empty();
    }

    // Additional Sponge-specific methods

    public Optional<ServerSideConnection> getConnection() {
        if (this.playerObject instanceof ServerPlayer) {
            return Optional.of(((ServerPlayer) this.playerObject).connection());
        }
        return Optional.empty();
    }

    public void teleport(ServerLocation location) {
        if (this.playerObject instanceof ServerPlayer) {
            ((ServerPlayer) this.playerObject).setLocation(location);
        }
    }

    public Optional<ServerLocation> getLocation() {
        if (this.playerObject instanceof ServerPlayer) {
            return Optional.of(((ServerPlayer) this.playerObject).serverLocation());
        }
        return Optional.empty();
    }


}