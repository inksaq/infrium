package com.infrium.core;

import lombok.NonNull;
import com.infrium.api.InfriumProvider;
import com.infrium.api.configuration.ConfigurationContainer;
import com.infrium.api.configuration.InfriumConfiguration;
import com.infrium.api.hive.ServerRepository;
import com.infrium.api.hive.servers.Server;
import com.infrium.api.player.AbstractInfriumPlayer;
import com.infrium.core.events.*;
import com.infrium.core.player.BukkitInfriumPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BukkitInfriumProvider extends InfriumProvider<Player> implements Listener {

  public BukkitInfriumProvider(ConfigurationContainer<YamlConfiguration> configurationContainer) throws IOException {
    super(configurationContainer);
  }

  @Override
  public List<Player> getOnlinePlayers() {
    return new ArrayList<>(Bukkit.getOnlinePlayers());
  }

  @Override
  public AbstractInfriumPlayer<Player> craftInfriumPlayer(@NonNull Player playerObject) {
    return new BukkitInfriumPlayer(playerObject);
  }

  @EventHandler
  public void onPlayerJoinEvent(@NonNull PlayerJoinEvent event) {
    Bukkit.getScheduler()
        .runTaskAsynchronously(ICore.getInstance(), () -> onJoin(event.getPlayer()));
    ICore.getInstance().setupPrefix(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuitEvent(@NonNull PlayerQuitEvent event) {
    Bukkit.getScheduler()
        .runTaskAsynchronously(ICore.getInstance(), () -> onQuit(event.getPlayer()));
  }

  @Override
  public @NonNull ServerRepository serverRepositoryBuilder() {
    return new BukkitServerRepository(
            InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
  }

  private final class BukkitServerRepository extends ServerRepository {

    public BukkitServerRepository(String redisUri, String mongoUri) {
      super(redisUri, mongoUri);
    }

    private void callEvent(Event e) {
      Bukkit.getScheduler()
          .runTask(ICore.getInstance(), () -> Bukkit.getPluginManager().callEvent(e));
    }


    @Override
    public void onServerAdd(@NonNull Server server) {
      callEvent(new OnServerAddEvent(server));

    }

    @Override
    public void onServerDelete(@NonNull Server server) {
      callEvent(new OnServerDeleteEvent(server));
    }

    @Override
    public void onServerUpdate(@NonNull Server server) {
      callEvent(new OnServerUpdateEvent(server));
    }

    @Override
    public void onSync() {
      callEvent(new OnSyncEvent());
    }
  }
}
