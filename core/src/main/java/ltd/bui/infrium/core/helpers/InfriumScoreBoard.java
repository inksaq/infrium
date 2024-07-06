package ltd.bui.infrium.core.helpers;

import ltd.bui.infrium.core.InfriumCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class InfriumScoreBoard {

  private static final Map<UUID, InfriumScoreBoard> players = new HashMap<>();
  private final Scoreboard scoreboard;
  private final Objective sidebar;
  private long startTime;
  private int tickRate;

  private InfriumScoreBoard(Player player) {
    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    sidebar = scoreboard.registerNewObjective("sidebar", "dummy");

    // Create Teams
    for (int i = 1; i <= 15; i++) {
      var team = scoreboard.registerNewTeam("SLOT_" + i);
      team.addEntry(genEntry(i));
    }
    player.setScoreboard(scoreboard);
    synchronized (InfriumScoreBoard.players) {
      players.put(player.getUniqueId(), this);
    }
  }

  public static boolean hasScore(Player player) {
    boolean contains;
    synchronized (InfriumScoreBoard.players) {
      contains = players.containsKey(player.getUniqueId());
    }
    return contains;
  }

  public static InfriumScoreBoard createScore(Player player) {
    return new InfriumScoreBoard(player);
  }

  public static InfriumScoreBoard getByPlayer(Player player) {
    InfriumScoreBoard cached;
    synchronized (InfriumScoreBoard.players) {
      cached = players.get(player.getUniqueId());
    }
    return cached;
  }

  public static void removeScore(Player player) {
    players.remove(player.getUniqueId());
  }

  public static void flush() {
    players.clear();
  }

  public void setTitle(String title) {
    title = title.length() > 128 ? title.substring(0, 128) : title;
    sidebar.displayName(LegacyComponentSerializer.legacyAmpersand()
            .deserialize(title));
    sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
  }

  public void setSlot(int slot, String text) {
    this.setSlot(slot, new ScoreboardRow(text));
  }

  public void setSlot(int slot, ScoreboardRow row) {
    Team team = scoreboard.getTeam("SLOT_" + slot);
    String entry = genEntry(slot);
    if (!scoreboard.getEntries().contains(entry)) {
      sidebar.getScore(entry).setScore(slot);
    }
    team.prefix(MiniMessage.miniMessage().deserialize(row.getPrefix()));
    team.suffix(MiniMessage.miniMessage().deserialize(row.getSuffix()));
  }

  public void removeSlot(int slot) {
    String entry = genEntry(slot);
    if (scoreboard.getEntries().contains(entry)) {
      scoreboard.resetScores(entry);
    }
  }

  public void setSlotsFromList(List<String> list) {
    ArrayList<ScoreboardRow> rows = new ArrayList<>();
    for (int i = 0; i < list.size() && i < 15; i++) {
      rows.add(new ScoreboardRow(list.get(i)));
    }
    this.setSlotsFromListRows(rows);
  }

  public void setSlotsFromListRows(List<ScoreboardRow> rows) {
    if (rows.size() > 15) {
      rows = rows.subList(0, 15);
    }

    int slot = rows.size();

    if (slot < 15) {
      for (int i = (slot + 1); i <= 15; i++) {
        removeSlot(i);
      }
    }

    for (ScoreboardRow row : rows) {
      setSlot(slot, row);
      slot--;
    }
  }


  private String genEntry(int slot) {
    return ChatColor.values()[slot].toString();
  }

  /**
   * @author serega6531
   * @lastEditor iim_rudy
   * @link https://gist.github.com/serega6531/4acd23ac188c8c568287
   */
  private class ScoreboardRow {

    private String prefix;
    private String suffix;

    public ScoreboardRow(String row) {
      row = ChatColor.translateAlternateColorCodes('&', row);
      if (row.length() <= 64) {
        prefix = row;
        suffix = "";
      } else { // up to 16+16, color pair is in single part
        int cut = findCutPoint(row);
        prefix = row.substring(0, cut);
        suffix = continueColors(prefix) + row.substring(cut);

        if (suffix.length() > 64) {
          suffix = suffix.substring(0, 64);
        }
      }
    }

    private int findCutPoint(String s) {
      for (int i = 64; i > 0; i--) {
        if (s.charAt(i - 1) == ChatColor.COLOR_CHAR && ChatColor.getByChar(s.charAt(i)) != null)
          continue;
        return i;
      }
      return 64;
    }

    private String continueColors(String prefix) {
      ChatColor activeColor = null;
      Set<ChatColor> formats = new HashSet<>();

      for (int i = 0; i < prefix.length() - 1; i++) {
        char c1 = prefix.charAt(i);
        char c2 = prefix.charAt(i + 1);

        ChatColor color = ChatColor.getByChar(c2);
        if (c1 == ChatColor.COLOR_CHAR && color != null) {
          if (color == ChatColor.RESET) {
            activeColor = null;
            formats.clear();
          } else if (color.isColor()) {
            activeColor = color;
          } else {
            formats.add(color);
          }
        }
      }

      StringBuilder sb = new StringBuilder();

      if (activeColor != null) sb.append(activeColor);
      formats.forEach(format -> sb.append(format.toString()));

      return sb.toString();
    }

    public String getPrefix() {
      return prefix;
    }

    public String getSuffix() {
      return suffix;
    }

    @Override
    public String toString() {
      return prefix + suffix;
    }
  }
}
