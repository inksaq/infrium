package com.infrium.core.player;

import com.infrium.api.player.AbstractInfriumPlayer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class BukkitInfriumPlayer extends AbstractInfriumPlayer<Player> {

  public BukkitInfriumPlayer(Player playerObject) {
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
    var time =
        Title.Times.of(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut));
    var t = Title.title(title, subtitle, time);
    this.playerObject.showTitle(t);
  }

  @Override
  public void disconnect(@NonNull Component component) {
    this.playerObject.kick(component);
  }

  @Override
  public String getUsername() {
    return this.playerObject.getName();
  }

  @Override
  public UUID getUniqueId() {
    return this.playerObject.getUniqueId();
  }

  @Override
  public boolean isOnline() {
    return this.playerObject.isOnline();
  }

  @Override
  public Optional<InetSocketAddress> getAddress() {
    return Optional.ofNullable(this.playerObject.getAddress());
  }
}
