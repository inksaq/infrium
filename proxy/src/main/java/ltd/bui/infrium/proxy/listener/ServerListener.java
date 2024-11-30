package ltd.bui.infrium.proxy.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import ltd.bui.infrium.api.hive.enums.QueueLeftReason;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.api.punishments.PunishmentType;
import ltd.bui.infrium.proxy.Proxy;
import ltd.bui.infrium.proxy.handler.QueueLimboHandler;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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
    System.out.println("register registrar");
    Proxy.get()
            .getServer()
            .getChannelRegistrar()
            .register(LEGACY_BUNGEE_CHANNEL, MODERN_BUNGEE_CHANNEL);
  }

  public static boolean checkPunishments(Player player2) {
    Optional<AbstractInfriumPlayer<Player>> infriumPlayer =
            Proxy.getInfriumProvider().getInfriumPlayer(player2);
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


  private boolean isServerTypeMatch(RegisteredServer server, ServerType type) {
    // Implementation depends on how you name your servers
    return server.getServerInfo().getName().toLowerCase().contains(type.name().toLowerCase());
  }

  @Subscribe
  public void onJoin(LoginEvent event) {
    Player player = event.getPlayer();
    String virtualHost = player.getVirtualHost().map(InetSocketAddress::getHostString).orElse("");
    if (virtualHost.equalsIgnoreCase("atm.infrium.com") || virtualHost.equalsIgnoreCase("192.168.1.41")) {
      // ATM server connection will be handled in onServerPreConnect
      return;
    }
    // todo
    checkPunishments(event.getPlayer());
  }

  @Subscribe
  public void onDisconnect(KickedFromServerEvent event) {
    // Get the player's previous server
    Optional<RegisteredServer> previousServer = event.getPlayer().getCurrentServer().flatMap(ServerConnection::getPreviousServer);

    // If the previous server is present, redirect the player to it
    if (previousServer.isPresent()) {
      event.setResult(KickedFromServerEvent.RedirectPlayer.create(previousServer.get(), Component.text("Redirecting to previous server...")));
    } else {
      // Handle case when no previous server is available
      event.getPlayer().disconnect(Component.text("No previous server available."));
    }
  }


  @Subscribe
  public void disconnect(DisconnectEvent event) {

    Proxy.get().unregisterQueueLimbo(event.getPlayer().getUsername());
    Proxy.get().getQueuedJoin().remove(event.getPlayer().getUsername());
    Proxy.get().getQueueRepository().leaveQueue(event.getPlayer().getUsername(), QueueLeftReason.DISCONNECTED);

    Proxy.get().getInfriumProvider().onQuit(event.getPlayer());
  }


  @Subscribe
  public void onServerPreConnect(ServerPreConnectEvent event) {
    Player player = event.getPlayer();
    RegisteredServer targetServer = event.getOriginalServer();
    String virtualHost = player.getVirtualHost().map(InetSocketAddress::getHostString).orElse("");

    if (virtualHost.equalsIgnoreCase("atm.infrium.com") || virtualHost.equalsIgnoreCase("192.168.1.41")) {
      handleAtmConnection(event, player);
    } else {
      var isQueued = Proxy.get().getQueuedJoin().get(event.getPlayer().getUsername());
      if (isQueued != null) {
        event.setResult(ServerPreConnectEvent.ServerResult.allowed(isQueued));
        Proxy.get().getQueuedJoin().remove(event.getPlayer().getUsername());
      }
    }
  }

  private void handleAtmConnection(ServerPreConnectEvent event, Player player) {
    Proxy.get().getServer().getServer("atm10").ifPresentOrElse(
            server -> {
              event.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
              player.sendMessage(Component.text("Connecting you to the ATM server..."));
            },
            () -> {
              event.setResult(ServerPreConnectEvent.ServerResult.denied());
              player.sendMessage(Component.text("ATM server is not available."));
            }
    );
  }

  @Subscribe
  public void onServerConnected(DisconnectEvent event) {
    System.out.println("disconnect: " + event.getPlayer().getUsername());
    System.out.println("reason: " + event.toString());
    // Handle server connection
  }

  @Subscribe
  public void onPostConnect(ServerConnectedEvent event) {
    var message =
            "\n                       &eLoading Shard - &l&e " + event.getServer().getServerInfo().getName() + " ... \n&7&oYour current game session has been paused while you are transferred.\n "
                    + event.getServer().getServerInfo().getName() + "\n";
    event.getPlayer().sendMessage(serialize.apply(message));
  }

  private boolean isModdedServer(RegisteredServer server) {
    // Implement logic to determine if a server is modded
    // This could be based on server name, properties, or a predefined list
    return server.getServerInfo().getName().contains("atm"); // Example condition
  }

  private RegisteredServer getAppropriateModdedServer(Player player) {
    // Implement logic to determine the appropriate modded server for the player
    // This could be based on player properties, load balancing, etc.
    return Proxy.get().getServer().getAllServers().stream()
            .filter(this::isModdedServer)
            .findFirst()
            .orElse(null);
  }


  @Subscribe
  public void pluginMessageEvent(final PluginMessageEvent event) {
    System.out.println(event.dataAsDataStream().readUTF());
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
      if (command == "test") {
        Proxy.get().getServer().sendMessage(Component.text("test"));
      }
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
        System.out.println("connect:server");
        System.out.println(command);
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
        System.out.println("send player to server");
      }
    }
  }

  @Subscribe
  public void onLoginLimboRegister(LoginLimboRegisterEvent event) {
    Player player = event.getPlayer();
    // Find an ATM server
    String virtualHost = event.getPlayer().getVirtualHost().map(h -> h.getHostName().toLowerCase()).orElse("");

    if (virtualHost.equalsIgnoreCase("atm.infrium.com") || virtualHost.equalsIgnoreCase("192.168.1.41")) {
      return;
    }
      event.addCallback(
              () -> {
                if (event.getPlayer().isActive()) {
                  Proxy.get().getQueueServer().spawnPlayer(event.getPlayer(), new QueueLimboHandler());
                }
              });
  }
}
