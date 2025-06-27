package com.infrium.proxy;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;

import lombok.NonNull;
import com.infrium.api.InfriumProvider;
import com.infrium.api.configuration.ConfigurationContainer;
import com.infrium.api.configuration.InfriumConfiguration;
import com.infrium.api.hive.ServerRepository;
import com.infrium.api.player.AbstractInfriumPlayer;
import com.infrium.proxy.player.ProxyInfriumPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProxyInfriumProvider extends InfriumProvider<Player> {

  public ProxyInfriumProvider(@NonNull ConfigurationContainer<?> configurationContainer) throws IOException {
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
