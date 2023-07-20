package ltd.bui.infrium.proxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.api.punishments.PunishmentType;
import ltd.bui.infrium.proxy.Proxy;

import java.util.Optional;

import static ltd.bui.infrium.proxy.Proxy.serialize;

public class PunishmentCommand implements SimpleCommand {

  @Override
  public void execute(Invocation invocation) {
    if (!hasPermission(invocation)) return;
    long duration = -1;
    PunishmentType punishmentType;
    String reason;

    AbstractInfriumPlayer<Player> issuer =
        invocation.source().equals(Proxy.get().getServer().getConsoleCommandSource())
            ? null
            : Proxy.get().getInfriumProvider().getInfriumPlayer((Player) invocation.source()).get();

    // playerTarget reason
    if (invocation.alias().equalsIgnoreCase("ban")) {
      punishmentType = PunishmentType.BAN;
      this.processPermanent(issuer, punishmentType, invocation.arguments());
    }
    if (invocation.alias().equalsIgnoreCase("kick")) {
      punishmentType = PunishmentType.KICK;
      this.processPermanent(issuer, punishmentType, invocation.arguments());
    }

    // playerTarget reason
    if (invocation.alias().equalsIgnoreCase("mute")) {
      punishmentType = PunishmentType.MUTE;
      this.processPermanent(issuer, punishmentType, invocation.arguments());
    }

    // playerTarget duration reason
    if (invocation.alias().equalsIgnoreCase("tempban")) {
      punishmentType = PunishmentType.BAN;
      return;
    }

    // playerTarget duration reason
    if (invocation.alias().equalsIgnoreCase("tempmute")) {
      punishmentType = PunishmentType.MUTE;
      return;
    }
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    if (invocation.source() instanceof Player) {
      Optional<AbstractInfriumPlayer<Player>> player =
          Proxy.get().getInfriumProvider().getInfriumPlayer((Player) invocation.source());
      return player.isPresent() && player.get().getPlayerData().getRank().isStaff();
    }
    return invocation.source().equals(Proxy.get().getServer().getConsoleCommandSource());
  }

  private void processPermanent(
      AbstractInfriumPlayer<Player> issuer, PunishmentType punishmentType, String... args) {
    if (args.length >= 1) {
      var oPlayer = Proxy.get().getServer().getPlayer(args[0]);
      if (oPlayer.isPresent()) {
        Optional<AbstractInfriumPlayer<Player>> oaap =
            Proxy.get().getInfriumProvider().getInfriumPlayer(oPlayer.get());
        if (oaap.isPresent()) {
          AbstractInfriumPlayer<Player> player = oaap.get();
          String reason = "Unknown Reason";
          if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
              reasonBuilder.append(args[i]).append(" ");
            }
            reason =
                reasonBuilder.substring(
                    0, reasonBuilder.length() - 1); // get string without last space
          }
          Proxy.get()
              .getInfriumProvider()
              .getPunishmentManager()
              .createPunishment(punishmentType, player, issuer, reason, -1);
          if (issuer == null) {
            Proxy.get()
                .getServer()
                .getConsoleCommandSource()
                .sendMessage(
                    serialize.apply(
                        "&a"
                            + punishmentType.name()
                            + " created for &b"
                            + player.getUsername()
                            + "&a."
                            + "&7Reason: &b"
                            + reason
                            + "&7."
                            + "&7Duration: &bPermanent&7."));
          } else {
            issuer.sendMessage(
                serialize.apply(
                    "&a"
                        + punishmentType.name()
                        + " created for &b"
                        + player.getUsername()
                        + "&a."
                        + "&7Reason: &b"
                        + reason
                        + "&7."
                        + "&7Duration: &bPermanent&7."));
          }

          if (punishmentType == PunishmentType.BAN || punishmentType == PunishmentType.KICK) {
            player.disconnect(serialize.apply(reason));
          }
        } else {
          // TODO: send message to issuer that server failed to fetch player's data
        }
      } else {
        // TODO: send message to issuer that player is not online
      }
    }
  }
}
