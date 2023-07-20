package ltd.bui.infrium.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.hive.servers.Server;
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServerRepository extends ServerRepository {

  private final Map<Server, RegisteredServer> proxyServerMap = new ConcurrentHashMap<>();

  public ProxyServerRepository(String redisUri, String mongoUri) {
    super(redisUri, mongoUri);
    this.onSync();
  }

    @Override
    public void onServerConnect(String message) {
      var server = message.split(":")[0];
      var player = message.split(":")[1];
      Proxy.get().getServer().getPlayer(player).get().createConnectionRequest(Proxy.get().getServer().getServer(server).orElseThrow()).fireAndForget();
    }

  @Override
  public void onServerAdd(@NonNull Server server) {
    proxyServerMap.computeIfAbsent(
        server,
        s -> {
          var info =
              new ServerInfo(
                  server.getName(), new InetSocketAddress(server.getIp(), server.getPort()));
          return Proxy.get().getServer().registerServer(info);
        });;
  }

  @Override
  public void onServerDelete(@NonNull Server server) {
    proxyServerMap.remove(server);
    Proxy.get()
        .getServer()
        .unregisterServer(
            new ServerInfo(
                server.getName(), new InetSocketAddress(server.getIp(), server.getPort())));
  }


  @Override
  public void onSync() {
    var servers = new ArrayList<>(getServers());

    // unregister dead servers
    for (var server2 : this.proxyServerMap.entrySet()) {
      if (!servers.contains(server2.getKey())) {
        Proxy.get().getServer().unregisterServer(server2.getValue().getServerInfo());
        this.proxyServerMap.remove(server2.getKey());
        Proxy.get()
            .getLogger()
            .info(
                "Unregistered a server #-> "
                    + server2.getKey().getName()
                    + "\t|"
                    + this.proxyServerMap.size());
      }
    }

    // register new servers
    for (Server server : servers) {
      proxyServerMap.computeIfAbsent(
          server,
          s -> {
            var info =
                new ServerInfo(
                    server.getName(), new InetSocketAddress(server.getIp(), server.getPort()));
            Proxy.get()
                .getLogger()
                .info(
                    "Registering a new server #-> "
                        + server.getName()
                        + "\t|"
                        + this.proxyServerMap.size());
            return Proxy.get().getServer().registerServer(info);
          });
    }
  }
}
