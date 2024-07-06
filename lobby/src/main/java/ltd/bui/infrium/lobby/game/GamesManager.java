package ltd.bui.infrium.lobby.game;

import ltd.bui.infrium.lobby.InfriumLobby;
import ltd.bui.infrium.lobby.game.s.FFALobbyGame;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamesManager {

  private final List<LobbyGame> gameList = new ArrayList<>();

  public GamesManager() {
    this.register(new FFALobbyGame());
  }

  private void register(LobbyGame g) {
    this.gameList.add(g);
    InfriumLobby.getInstance().getServer().getPluginManager().registerEvents(g, InfriumLobby.getInstance());
    InfriumLobby.getInstance().getLogger().info("Registered game: " + g.getClass().getName());
  }

  public boolean isPlayerPlaying(Player p) {
    for (LobbyGame g : this.gameList) {
      if (g.isPlayerPlaying(p)) {
        return true;
      }
    }
    return false;
  }
}
