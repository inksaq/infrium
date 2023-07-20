package ltd.bui.infrium.core.helpers;

import lombok.NonNull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MessageUtils {

  public static final char BAR_CHAR = '\u2588'; // full block character
  public static final char HEAVY_VERTICAL = '\u2503'; // box drawings heavy vertical character
  private static final int CENTER_PX = 154;

  /**
   * send chat Message to a player
   *
   * @param message message to sand
   * @param type alignment
   * @param players player list
   */
  public static void sendChatMessage(
      @NonNull String message, @NonNull AlignmentType type, Player... players) {
    message = ChatColor.translateAlternateColorCodes('&', message); // fix colors
    if (type == AlignmentType.CENTERED) {
      message = centerMessage(message);
    }
    // send message
    for (Player p : players) {
      p.sendMessage(message);
    }
  }

  /**
   * send chat Message to a player
   *
   * @param message message to sand
   * @param type alignment
   * @param players player list
   */
  public static void sendChatMessagePlayers(
      String message, AlignmentType type, Collection<Player> players) {
    message = ChatColor.translateAlternateColorCodes('&', message); // fix colors
    if (type == AlignmentType.CENTERED) {
      message = centerMessage(message);
    }
    // send message
    for (Player p : players) {
      p.sendMessage(message);
    }
  }

  /**
   * Send actionbar to players
   *
   * @param message actionbar message
   * @param players to players
   */
  public static void sendActionbar(@NonNull String message, Player... players) {
//    message = ChatColor.translateAlternateColorCodes('&', message); // fix colors
    for (Player p : players) {
      p.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
  }

  /**
   * Send player actionbar percentage
   *
   * @param c1 percentage color
   * @param c2 inactive color
   * @param percentage actionbar percentage
   * @param players player list
   */
  public static void sendActionbarPercentagePrefix(
      ChatColor c1, ChatColor c2, int percentage, @NonNull Player... players) {
    sendActionbarPercentage(BAR_CHAR, "", "", c1, c2, percentage, players);
  }

  /**
   * Send player actionbar percentage
   *
   * @param prefix prefix message
   * @param c1 percentage color
   * @param c2 inactive color
   * @param percentage actionbar percentage
   * @param players player list
   */
  public static void sendActionbarPercentagePrefix(
      String prefix, ChatColor c1, ChatColor c2, int percentage, @NonNull Player... players) {
    sendActionbarPercentage(BAR_CHAR, prefix, "", c1, c2, percentage, players);
  }

  /**
   * Send player actionbar percentage
   *
   * @param suffix message suffix
   * @param c1 percentage color
   * @param c2 inactive color
   * @param percentage actionbar percentage
   * @param players player list
   */
  public static void sendActionbarPercentageSuffix(
      String suffix, ChatColor c1, ChatColor c2, int percentage, @NonNull Player... players) {
    sendActionbarPercentage(BAR_CHAR, "", suffix, c1, c2, percentage, players);
  }

  /**
   * Send player actionbar percentage appending a message specifying where
   *
   * @param character bar character
   * @param prefix message prefix
   * @param suffix message suffix
   * @param c1 percentage color
   * @param c2 inactive color
   * @param percentage actionbar percentage
   * @param players player list
   */
  public static void sendActionbarPercentage(
      char character,
      String prefix,
      String suffix,
      @NonNull ChatColor c1,
      @NonNull ChatColor c2,
      int percentage,
      @NonNull Player... players) {
    StringBuilder message = new StringBuilder();
    message.append(String.valueOf(character).repeat(10));
    // 100 : messageLenght = percentage : x
    int amount = (message.length() * percentage) / 100;
    message = new StringBuilder(c1 + message.substring(0, amount) + c2 + message.substring(amount));

    if (prefix != null) {
      message.insert(0, prefix);
    }
    if (suffix != null) {
      message.append(suffix);
    }
    sendActionbar(message.toString(), players);
  }

  /**
   * Send player actionbar percentage appending a message specifying where
   *
   * @param prefix message prefix
   * @param suffix message suffix
   * @param c1 percentage color
   * @param c2 inactive color
   * @param percentage actionbar percentage
   * @param players player list
   */
  public static void sendActionbarPercentage(
      String prefix,
      String suffix,
      @NonNull ChatColor c1,
      @NonNull ChatColor c2,
      int percentage,
      @NonNull Player... players) {
    sendActionbarPercentage(BAR_CHAR, prefix, suffix, c1, c2, percentage, players);
  }

  public static String centerMessage(@NonNull String message) {

    if (message.isEmpty()) return message;
    message = ChatColor.translateAlternateColorCodes('&', message);

    int messagePxSize = 0;
    boolean previousCode = false;
    boolean isBold = false;

    for (char c : message.toCharArray()) {
      if (c == ChatColor.COLOR_CHAR) {
        previousCode = true;
      } else if (previousCode) {
        previousCode = false;
        isBold = c == 'l' || c == 'L';
      } else {
        DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
        messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
        messagePxSize++;
      }
    }

    int halvedMessageSize = messagePxSize / 2;
    int toCompensate = CENTER_PX - halvedMessageSize;
    int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
    int compensated = 0;
    StringBuilder sb = new StringBuilder();
    while (compensated < toCompensate) {
      sb.append(" ");
      compensated += spaceLength;
    }
    return sb + message;
  }

  public enum AlignmentType {
    NORMAL,
    CENTERED
  }
}
