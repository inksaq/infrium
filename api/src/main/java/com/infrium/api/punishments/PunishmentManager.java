package com.infrium.api.punishments;

import lombok.NonNull;
import com.infrium.api.InfriumProvider;
import com.infrium.api.player.AbstractInfriumPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public class PunishmentManager {

  private final InfriumProvider provider;

  public PunishmentManager(InfriumProvider provider) {
    this.provider = provider;
  }

  public Optional<Punishment> createPunishment(
      @NonNull PunishmentType type,
      @NonNull AbstractInfriumPlayer player,
      AbstractInfriumPlayer issuer,
      @NonNull String reason,
      @NonNull long duration) {
    String issuerName = "Console";
    if (issuer != null) {
      if (!issuer.getPlayerData().getRank().isStaff()) {
        issuer.sendMessage(MiniMessage.miniMessage().deserialize("Unknown command."));
        return Optional.empty();
      }
      issuerName = issuer.getUsername();
    }
    Punishment punishment =
        new Punishment(
            type,
            reason,
            System.currentTimeMillis(),
                duration <= 0 ? 7889400 : duration,
            issuerName,
            player.getUsername(),
            type != PunishmentType.KICK);
    player.getPlayerData().getPunishmentList().add(punishment);
    provider.saveInfriumPlayer(player);
    return Optional.of(punishment);
  }
}
