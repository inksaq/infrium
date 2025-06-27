package com.infrium.game.components.weapon.energy.components.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreComponentLogger {
    private static final Logger logger = Logger.getLogger("CoreComponent");

    public static void log(Level level, CoreComponentType componentType, String message) {
        logger.log(level, String.format("[%s] %s", componentType, message));
    }

    public static void info(CoreComponentType componentType, String message) {
        log(Level.INFO, componentType, message);
    }

    public static void warning(CoreComponentType componentType, String message) {
        log(Level.WARNING, componentType, message);
    }

    public static void severe(CoreComponentType componentType, String message) {
        log(Level.SEVERE, componentType, message);
    }

    public static void debug(CoreComponentType componentType, String message) {
        log(Level.FINE, componentType, message);
    }
}
