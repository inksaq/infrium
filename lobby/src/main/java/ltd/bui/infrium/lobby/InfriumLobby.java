package ltd.bui.infrium.lobby;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import ltd.bui.infrium.core.configuration.YamlConfigurationContainer;
import ltd.bui.infrium.core.helpers.InfriumScoreBoard;
import ltd.bui.infrium.core.commands.ServerCommand;
import ltd.bui.infrium.lobby.configuration.LobbyConfiguration;
import ltd.bui.infrium.lobby.game.GamesManager;
import ltd.bui.infrium.lobby.gui.LobbySelectorGUI;
import ltd.bui.infrium.core.gui.ServerSelectorGUI;
import ltd.bui.infrium.lobby.listener.PlayerListener;
import ltd.bui.infrium.lobby.staff.BuildCommand;
import ltd.bui.infrium.lobby.staff.LobbyManagerCommand;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class InfriumLobby extends JavaPlugin {

  private static final String[] animations =
      new String[] {
        "&5&lASYLUM.EU", "&5&lASYLUM.EU %playerlist_online,normal,yes,amount%", "&5&lASYLUM.EU"
      };

  @Getter private static InfriumLobby instance;
  @Getter private final List<Player> buildingPlayers = new ArrayList<>();
  private LobbySelectorGUI lobbySelectorGUI;
  @Getter private Location lobbyLocation;
  @Getter private GamesManager gamesManager;
  private int animationTick = 0;
  private BukkitCommandManager commandManager;
  private YamlConfigurationContainer configuration;
  private List<String> scoreboardList = new ArrayList<>();
  private String scoreboardTitle;
  private final Runnable scoreboardTask =
      () -> {
        Bukkit.getOnlinePlayers()
            .forEach(
                player -> {
                  InfriumScoreBoard board = InfriumScoreBoard.getByPlayer(player);

                  if (board == null) return;

                  if (!this.scoreboardTitle.isEmpty()) {
                    board.setTitle(PlaceholderAPI.setPlaceholders(player, this.scoreboardTitle));
                  } else {
                    board.setTitle(
                        PlaceholderAPI.setPlaceholders(player, animations[animationTick]));
                  }
                  List<String> placeholdered = new ArrayList<>();
                  for (var s : scoreboardList) {
                    placeholdered.add(PlaceholderAPI.setPlaceholders(player, s));
                  }
                  board.setSlotsFromList(placeholdered);
                });

        animationTick++;
        if (animationTick >= animations.length) animationTick = 0;
      };

  @Override
  public void onEnable() {
    InfriumLobby.instance = this;
    this.getServer().getScheduler().runTaskTimerAsynchronously(this, scoreboardTask, 10L, 60L);
    this.commandManager = new BukkitCommandManager(this);
    this.commandManager.registerCommand(new LobbyManagerCommand());
    this.commandManager.registerCommand(new BuildCommand());
    File path = new File(getDataFolder(), "InfriumLobby.yml");

    try {
      if (!path.exists()) {
        getDataFolder().mkdirs();
        path.createNewFile();
      }
      YamlConfiguration yamlConfiguration = new YamlConfiguration();
      yamlConfiguration.load(path);
      this.configuration = new YamlConfigurationContainer(yamlConfiguration, path);
    } catch (Exception e) {
      throw new RuntimeException(e); // re throw exception so the plugin will be disabled
    }

    this.loadData();
    this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    this.lobbySelectorGUI =
        new LobbySelectorGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Lobby Shards"),
                this);
    this.gamesManager = new GamesManager();
    this.getServer().getOnlinePlayers().forEach(Items::formatInventory);

  }

  public void reload() throws Exception {
    this.configuration.reload(false);
    this.loadData();
  }

  private void loadData() {
    List<String> s = LobbyConfiguration.SCOREBOARD.get(List.class);
    this.scoreboardTitle = s.get(0);
    this.scoreboardList = s.subList(1, s.size());
    this.lobbyLocation = LobbyConfiguration.HUB_SPAWN.get(Location.class);
  }

  @Override
  public void onDisable() {
    this.commandManager.unregisterCommands();
    try {
      this.configuration.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setLobbyLocation(Location lobbyLocation) {
    LobbyConfiguration.HUB_SPAWN.set(lobbyLocation);
    this.lobbyLocation = lobbyLocation;
  }
}
