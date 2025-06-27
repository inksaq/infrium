package com.infrium.game.util;

import java.util.Random;
import java.util.UUID;

public class NumberHelper {
    private static final Random random = nextRandom();

    public static int ceil(final double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int floor(final double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static Random nextRandom() {
        return new XORShiftRNG();
    }

    public static UUID nextUUID() {
        return new UUID(random.nextLong(), random.nextLong());
    }

    public static double random() {
        return random.nextDouble();
    }

    public static int round(double num) {
        return floor(num + 0.5d);
    }

    public static boolean toBoolean(Object object) {
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        switch(object.toString().toLowerCase()) {
            case "true":
                return true;
            case "yes":
                return true;
            case "1":
                return true;
            case "y":
                return true;
            case "sure":
                return true;
            case "kk":
                return true;
            case "ok":
                return true;
            case "okay":
                return true;
            case "please":
                return true;
            case "confirm":
                return true;
            case "accept":
                return true;
            case "correct":
                return true;
            default:
                return false;
        }
    }

    public static double toByte(Object object) {
        return toByte(object, (byte) 0);
    }

    public static byte toByte(Object object, byte defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }
        try {
            return Byte.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }

    public static double toDouble(Object object) {
        return toDouble(object, 0d);
    }

    public static double toDouble(Object object, double defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        try {
            return Double.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }

    public static float toFloat(Object object) {
        return toFloat(object, 0f);
    }

    public static float toFloat(Object object, float defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }
        try {
            return Float.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }

    public static int toInt(Object object) {
        return toInt(object, 0);
    }

    public static int toInt(Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        try {
            return Integer.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }

    public static long toLong(Object object) {
        return toLong(object, 0l);
    }

    public static long toLong(Object object, long defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        try {
            return Long.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }

    public static short toShort(Object object) {
        return toShort(object, (short) 0);
    }

    public static short toShort(Object object, short defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }
        try {
            return Short.valueOf(object.toString());
        } catch (Exception e) {}
        return defaultValue;
    }
}
