package ltd.bui.infrium.game.util;

public enum Carg {
    OPTIONAL(0, "Optional"),
    MESSAGE(1, "Message"),
    STRING(2, "String"),
    BYTE(3, "Byte"),
    SHORT(4, "Short"),
    INT(5, "Int"),
    FLOAT(6, "Float"),
    DOUBLE(7, "Double"),
    LONG(8, "Long"),
    BOOLEAN(9, "Boolean");

    private final int id;
    private final String name;

    Carg(int id, String name) {
        this.id = id;
        this.name = name.toLowerCase();
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Carg get(String name) {
        for(Carg param : Carg.values()) {
            if(name.equalsIgnoreCase(param.name)) {
                return param;
            }
        }
        return Carg.STRING;
    }
}
