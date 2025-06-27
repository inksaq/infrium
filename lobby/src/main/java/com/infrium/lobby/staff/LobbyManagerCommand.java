package com.infrium.lobby.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.infrium.api.util.NekobinUploader;
import com.infrium.core.helpers.MessageUtils;
import com.infrium.lobby.InfriumLobby;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandAlias("lobbymanager|hubmanager|hm|lm")
public class LobbyManagerCommand extends BaseCommand {

  @Subcommand("setspawn|sethub|setlobby")
  @CommandAlias("sethub|setlobby")
  public void setLobby(Player player) {
    var ap = com.infrium.core.ICore.getInstance().getInfriumProvider().getInfriumPlayer(player);
    if (ap.isEmpty() || !ap.get().getPlayerData().getRank().isOpOrCaretaker()) return;

    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', "&aLobby set to your current location."));
    InfriumLobby.getInstance().setLobbyLocation(player.getLocation());
    try {
      InfriumLobby.getInstance().getConfiguration().save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subcommand("reload")
  public void reloadConfiguration(Player player) {
    var ap = com.infrium.core.ICore.getInstance().getInfriumProvider().getInfriumPlayer(player);
    if (ap.isEmpty() || !ap.get().getPlayerData().getRank().isOpOrCaretaker()) return;

    player.sendMessage(
        ChatColor.translateAlternateColorCodes(
            '&', "&6&lArcade &8" + MessageUtils.HEAVY_VERTICAL + " &7Reloading configuration..."));
    try {
      InfriumLobby.getInstance().reload();
      player.sendMessage(
          ChatColor.translateAlternateColorCodes(
              '&',
              "&6&lArcade &8"
                  + MessageUtils.HEAVY_VERTICAL
                  + " &7Configuration reloaded &a&nsuccessfully&8."));
    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(
          ChatColor.translateAlternateColorCodes(
              '&',
              "&6&lArcade &8"
                  + MessageUtils.HEAVY_VERTICAL
                  + " &7Something went wrong wile reloading the configuration. (Watch console for errors)"));
      NekobinUploader.upload(ExceptionUtils.getStackTrace(e))
          .thenAccept(
              result -> {
                Bukkit.getLogger()
                    .warning(
                        "Uploaded error to nekobin: " + result.isOk() + "   " + result.getError());
                if (result.isOk()) {
                  player.sendMessage(
                      ChatColor.translateAlternateColorCodes(
                          '&',
                          "&6&lArcade &8"
                              + MessageUtils.HEAVY_VERTICAL
                              + " &7Nekobin link: &a"
                              + result.getDocument().asUrl()));
                }
              });
    }
  }

  @Subcommand("throwexcp")
  public void throwException(Player player) {
    var ap = com.infrium.core.ICore.getInstance().getInfriumProvider().getInfriumPlayer(player);
    if (ap.isEmpty() || !ap.get().getPlayerData().getRank().isOpOrCaretaker()) return;
    throw new RuntimeException("SOME EXCEPTION " + System.currentTimeMillis());
  }
}
