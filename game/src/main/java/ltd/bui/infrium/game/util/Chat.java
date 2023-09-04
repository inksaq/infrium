package ltd.bui.infrium.game.util;

public enum Chat {
    /** Allowed characters
     !"#$%&'()*+,-./
     0123456789:;<=>?
     @ABCDEFGHIJKLMNO
     PQRSTUVWXYZ[\]^_
     'abcdefghijklmno
     pqrstuvwxyz{|}~⌂
     ÇüéâäàåçêëèïîìÄÅ
     ÉæÆôöòûùÿÖÜø£Ø×ƒ
     áíóúñÑªº¿®¬½¼¡«»

     Color guide: http://ess.khhq.net/mc/
     */

    // Dark
    DARK_RED(0, '4'),
    DARK_GREEN(1, '2'),
    DARK_GRAY(2, '8'),
    DARK_AQUA(3, '3'),
    DARK_BLUE(4, '1'),
    // Normal
    PURPLE(5, '5'),
    RED(6, 'c'),
    GOLD(7, '6'),
    YELLOW(8, 'e'),
    GREEN(9, 'a'),
    AQUA(10, 'b'),
    BLUE(11, '9'),
    PINK(12, 'd'),
    WHITE(13, 'f'),
    GRAY(14, '7'),
    BLACK(15, '0'),
    // Special
    BOLD(16, 'l'),
    UNDERLINE(17, 'n'),
    ITALIC(18, 'o'),
    MAGIC(19, 'k'),
    STRIKE(20, 'm'),
    RESET(21, 'r');

    public static final char COLOR_CHAR = '\u00A7'; // §
    // Error
    public static String msgError = Chat.DARK_RED + "" + Chat.BOLD + "[" + Chat.RED + "" + Chat.BOLD + "x" + Chat.DARK_RED + "" + Chat.BOLD + "]" + Chat.RED + " ";
    public static String lineError = "  " + Chat.DARK_RED +"»" + Chat.RED + " ";
    public static String formatError = Chat.DARK_RED + "$0" + Chat.RED;
    // Warn
    public static String msgWarn = Chat.GOLD + "" + Chat.BOLD + "[" + Chat.YELLOW + "" + Chat.BOLD + "x" + Chat.GOLD + "" + Chat.BOLD + "]" + Chat.YELLOW + " ";
    public static String lineWarn = "  " + Chat.GOLD +"»" + Chat.YELLOW + " ";
    public static String formatWarn = Chat.GOLD + "$0" + Chat.YELLOW;
    // Info
    public static String msgInfo = Chat.DARK_GREEN + "" + Chat.BOLD + "[" + Chat.GREEN + "" + Chat.BOLD + "x" + Chat.DARK_GREEN + "" + Chat.BOLD + "]" + Chat.GREEN + " ";
    public static String lineInfo = "  " + Chat.DARK_GREEN +"»" + Chat.GREEN + " ";
    public static String formatInfo = Chat.DARK_GREEN + "$0" + Chat.GREEN;
    // Passive
    public static String msgPassive = Chat.DARK_GRAY + "" + Chat.BOLD + "[" + Chat.GRAY + "" + Chat.BOLD + "x" + Chat.DARK_GRAY + "" + Chat.BOLD + "]" + Chat.GRAY + " ";
    public static String linePassive = "  " + Chat.DARK_GRAY +"»" + Chat.GRAY + " ";
    public static String formatPassive = Chat.DARK_GRAY + "$0" + Chat.GRAY;
    // Global
    public static String msgGlobal = Chat.DARK_AQUA + "" + Chat.BOLD + "[" + Chat.AQUA + "" + Chat.BOLD + 'G' + Chat.DARK_AQUA + "" + Chat.BOLD + "]" + Chat.RESET + " ";
    public static String lineGlobal = "  " + Chat.DARK_AQUA +"»" + Chat.AQUA + " ";
    public static String formatGlobal = Chat.DARK_AQUA + "$0" + Chat.AQUA;

    public final int id;
    public final char color;
    private final String string;

    private Chat(int index, char color) {
        this.id = index;
        this.color = color;
        this.string = COLOR_CHAR + "" + color;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return string;
    }

    public static Chat fromID(int id) {
        for(Chat chat : Chat.values()) {
            if(chat.id == id) {
                return chat;
            }
        }
        throw new IllegalArgumentException("Invalid ID of " + id);
    }
}

