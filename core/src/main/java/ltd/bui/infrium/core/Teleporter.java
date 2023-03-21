package ltd.bui.infrium.core;

import ltd.bui.infrium.api.hive.enums.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Teleporter {

  private static void sendPacketToProxy(String... data) {
    final ByteArrayOutputStream b = new ByteArrayOutputStream();
    final DataOutputStream out = new DataOutputStream(b);
    try {
      for (var d : data) {
        out.writeUTF(d);
      }
      Bukkit.getOnlinePlayers().stream()
          .findFirst()
          .ifPresent(
              player -> {
                player.sendPluginMessage(InfriumCore.getInstance(), "BungeeCord", b.toByteArray());
              });
    } catch (Exception e) {
      e.printStackTrace();
      InfriumCore.getInstance().getLogger().severe("Failed to send packet to proxy!");
    }
  }

  public static void joinQueue(Player player, ServerType serverType) {
    sendPacketToProxy("hive", "queue:join", serverType.name(), player.getName());
  }

  public static void leaveQueue(Player player) {
    sendPacketToProxy("hive", "queue:leave", player.getName());
  }

  public static void joinQueueLobby(Player player) {
    joinQueue(player, ServerType.LOBBY);
  }

  public static void connect(Player player, String serverName) {
    sendPacketToProxy("hive", "connect:server", serverName, player.getName());
  }
}
