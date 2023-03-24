package ltd.bui.infrium.proxy.listener;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ltd.bui.infrium.api.hive.enums.QueueLeftReason;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.api.punishments.PunishmentType;
import ltd.bui.infrium.proxy.Proxy;
import ltd.bui.infrium.proxy.handler.QueueLimboHandler;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;


import java.util.Optional;

import static ltd.bui.infrium.proxy.Proxy.serialize;


public class ServerListener {

  public static final char BAR_CHAR = '\u2588'; // full block character
  public static final char HEAVY_VERTICAL = '\u2503'; // box drawings heavy vertical character
  private static final LegacyChannelIdentifier LEGACY_BUNGEE_CHANNEL =
      new LegacyChannelIdentifier("BungeeCord");
  private static final MinecraftChannelIdentifier MODERN_BUNGEE_CHANNEL =
      MinecraftChannelIdentifier.create("bungeecord", "main");

  public ServerListener() {
    Proxy.get()
        .getServer()
        .getChannelRegistrar()
        .register(LEGACY_BUNGEE_CHANNEL, MODERN_BUNGEE_CHANNEL);
  }

  public static boolean checkPunishments(Player player2) {
    Optional<AbstractInfriumPlayer<Player>> infriumPlayer =
        Proxy.get().getInfriumProvider().getInfriumPlayer(player2);
    if (infriumPlayer.isPresent()) {
      var player = infriumPlayer.get();
      var optionalPunishment = player.getPlayerData().hasPunishmentActive(PunishmentType.BAN);
      if (optionalPunishment.isPresent()) {
        player2.disconnect(serialize.apply(optionalPunishment.get().getReason()));
        return true;
      }
    } else {
      player2.disconnect(serialize.apply("Cannot load player data, please try again later."));
      return true;
    }
    return false;
  }

  @Subscribe
  public void onJoin(LoginEvent event) {
    // todo
    checkPunishments(event.getPlayer());
  }

  @Subscribe
  public void disconnect(DisconnectEvent event) {
    Proxy.get().unregisterQueueLimbo(event.getPlayer().getUsername());
    Proxy.get().getQueuedJoin().remove(event.getPlayer().getUsername());
    Proxy.get()
        .getQueueRepository()
        .leaveQueue(event.getPlayer().getUsername(), QueueLeftReason.DISCONNECTED);

    Proxy.get().getInfriumProvider().onQuit(event.getPlayer());
  }

  @Subscribe
  public void onServerPreConnect(ServerPreConnectEvent event) {
    var isQueued = Proxy.get().getQueuedJoin().get(event.getPlayer().getUsername());
    if (isQueued != null) {
      event.setResult(ServerPreConnectEvent.ServerResult.allowed(isQueued));
      Proxy.get().getQueuedJoin().remove(event.getPlayer().getUsername());
    }
  }

  @Subscribe
  public void onPostConnect(ServerConnectedEvent event) {
    var message =
        "&b&lTeleporter &8"
            + HEAVY_VERTICAL
            + " &7Connected to &a"
            + event.getServer().getServerInfo().getName();
    if (event.getPreviousServer().isPresent()) {
      message += " &7from &6" + event.getPreviousServer().get().getServerInfo().getName();
    }
    event.getPlayer().sendMessage(serialize.apply(message));
  }

  @Subscribe
  public void pluginMessageEvent(final PluginMessageEvent event) {
    if (!event.getIdentifier().equals(LEGACY_BUNGEE_CHANNEL)
        && !event.getIdentifier().equals(MODERN_BUNGEE_CHANNEL)) {
      return;
    }
    event.setResult(PluginMessageEvent.ForwardResult.handled());
    if (!(event.getSource() instanceof ServerConnection)) {
      return;
    }

    ByteArrayDataInput in = event.dataAsDataStream();
    event.setResult(PluginMessageEvent.ForwardResult.handled());
    String subChannel = in.readUTF();

    if (subChannel.equals("hive")) {
      String command = in.readUTF();
      if (command.equals("queue:join")) { // join queue for a server
        String serverType = in.readUTF();
        String playerName = in.readUTF();
        try {
          ServerType type = ServerType.valueOf(serverType);
          Proxy.get().getQueueRepository().joinQueue(playerName, type);
        } catch (Exception e) {
          // server type not found
        }
      } else if (command.equals("queue:leave")) { // leave queue for a server
        String playerName = in.readUTF();
        Proxy.get().getQueueRepository().leaveQueue(playerName);
      } else if (command.equals("connect:server")) { // connect to a server
        String serverName = in.readUTF();
        String playerName = in.readUTF();
        Proxy.get()
            .getServer()
            .getPlayer(playerName)
            .ifPresent(
                player -> {
                  Proxy.get()
                      .getServer()
                      .getServer(serverName)
                      .ifPresent(
                          server -> { // if server is present
                            Proxy.get()
                                .getQueueLimboHandler(player.getUsername())
                                .ifPresentOrElse(
                                    limbo -> limbo.getPlayer().disconnect(server),
                                    () -> player.createConnectionRequest(server).fireAndForget());
                          });
                });
      }
    }
  }

  @Subscribe
  public void onLoginLimboRegister(LoginLimboRegisterEvent event) {
    event.addCallback(
        () -> {
          if (event.getPlayer().isActive()) {
            Proxy.get().getQueueServer().spawnPlayer(event.getPlayer(), new QueueLimboHandler());
          }
        });
  }
}
