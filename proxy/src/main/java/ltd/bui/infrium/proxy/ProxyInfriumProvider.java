package ltd.bui.infrium.proxy;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;

import lombok.NonNull;
import ltd.bui.infrium.api.InfriumProvider;
import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import ltd.bui.infrium.api.configuration.InfriumConfiguration;
import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.proxy.player.ProxyInfriumPlayer;

import java.util.ArrayList;
import java.util.List;

public class ProxyInfriumProvider extends InfriumProvider<Player> {

  public ProxyInfriumProvider(@NonNull ConfigurationContainer<?> configurationContainer) {
    super(configurationContainer);
  }

  @Override
  public List<Player> getOnlinePlayers() {
    return new ArrayList<>(Proxy.get().getServer().getAllPlayers());
  }

  @Override
  public AbstractInfriumPlayer<Player> craftInfriumPlayer(@NonNull Player playerObject) {
    return new ProxyInfriumPlayer(playerObject);
  }

  @Subscribe
  public void onLoginEvent(LoginEvent event) {
    onJoin(event.getPlayer());
  }

  @Subscribe
  public void onDisconnectEvent(DisconnectEvent event) {
    onQuit(event.getPlayer());
  }

  @Override
  public ServerRepository serverRepositoryBuilder() {
    return new ProxyServerRepository(
            InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
  }
}
