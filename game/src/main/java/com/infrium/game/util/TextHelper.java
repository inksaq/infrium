package com.infrium.game.util;

import java.util.regex.Pattern;

public class TextHelper {
    public static final Pattern chatPattern = Pattern.compile("&([A-FK-OR0-9])", Pattern.CASE_INSENSITIVE);
    public static final Pattern chatPlanetMinecraft = Pattern.compile("pl.n.t.?m.n.cr.ft", Pattern.CASE_INSENSITIVE);
    public static final Pattern punctuationPattern = Pattern.compile("[\\])\"'(\\:\\.\\[|,></_+!=-]");
    public static final Pattern splitPattern = Pattern.compile("\\:");
    public static final Pattern spacePattern = Pattern.compile(" ");

    private static final Chat[] rainbowOrder = {Chat.DARK_RED, Chat.RED, Chat.GOLD,
            Chat.YELLOW, Chat.GREEN, Chat.DARK_GREEN, Chat.AQUA, Chat.DARK_AQUA
            , Chat.BLUE, Chat.DARK_BLUE, Chat.PURPLE, Chat.PINK};
    private static final String[] planetMinecraftBank = {"Serbia", "downtown Brooklyn", "the Sims", "Jupiter",
            "Neptune", "Cornwall", "Arkansas", "St. Helen's", "the Jersey Shore", "the past", "Black Rock Shooter",
            "Japan, the land of kawaii", "the Horde", "the Alliance", "the internet", "the Island of Misfit Toys",
            "this server", "China", "the Astral Plane", "Narnia", "the Planet of the Apes", "Fooly Cooly",
            "the movie Inception", "the Twilight series", "the Church of Scientology",
            "Texas", "Lucky Star", "my mother's basement", "the land of \"/rules? what /rules?\"",
            "Croydon", "the Consortium of Allied Griefers", "the Desert Well Building Committee",
            "the Village Green Preservation Society", "a land down under", "Narnia",
            "my dirt house near spawn", "your bathroom", "that special place in your heart",
            "a shoebox by the side of the road", "the only gay bar in the whole town",
            "the discarded carcass of a narwhal", "the mental hospital", "Nigeria",
            "the IRS", "the Starship Enterprise", "the Malaysian Embassy", "France",
            "the dark side of the moon", "the dump", "the middle of a riddle", "marklar",
            "Battersea Dog's Home, please adopt me :(", "Tipperary", "next door",
            "Canada", "the local soup kitchen", "just around the block", "the local homeless shelter",
            "the future", "the Vatican", "your worst nightmares", "a galaxy far, far away",
            "a rolled-up carpet in a skip", "Hogwarts", "an obscure 80's band", "Ikea",
            "Gandalf's army", "Dumbledore's Army", "the FBI", "the sewers", "CubedHost",
            "the Skylands world", "a Happy Meal box in the middle of the motorway", "a farm",
            "Mexico", "Taco Bell", "the local Chess club", "Sweden", "the Land of Cows",
            "the restaurant at the end of the universe", "the hack forums"};

    public static String editPlanetMinecraft(String string) {
        return chatPlanetMinecraft.matcher(string).replaceAll(planetMinecraftBank[(int)(NumberHelper.random() * planetMinecraftBank.length)]);
    }

    public static String editColor(String string) {
        return chatPattern.matcher(string).replaceAll(Chat.COLOR_CHAR + "$1");
    }

    public static String toRainbow(String string) {
        String newstring = "";
        for(int i = 0; i < string.length(); i++) {
            newstring += rainbowOrder[i % rainbowOrder.length] + "" + Chat.BOLD + string.charAt(i);
        }
        return newstring;
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }
        return titleCase.toString();
    }

    /**
     * If reset is true, shrink will at the color reset code if there is room.
     */
    public static String shrink(String string, boolean reset) {
        if(string.length() > 16) {
            string = string.substring(0, 16);
        } else if(reset) {
            if(string.length() < 15){
                string = string + Chat.RESET;
            }
        }
        return string;
    }

    public static String merge(String[] arguments, int startIndex) {
        String ret = "";
        for (int i = startIndex; i < arguments.length; i++) {
            ret += arguments[i] + " ";
        }
        return ret.trim();
    }

    /**
     * Basically returns the number of edits that have to be made
     * to turn String 's' into String 't'.
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("El null pointer");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }
        int p[] = new int[n + 1];
        int d[] = new int[n + 1];
        int _d[];
        int i;
        int j;
        char t_j;
        int cost;
        for (i = 0; i <= n; i++) {
            p[i] = i;
        }
        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }
}
