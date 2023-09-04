package ltd.bui.infrium.game;


import lombok.Getter;
import ltd.bui.infrium.core.api.command.CommandFramework;
import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.core.api.handlers.Handler;
import ltd.bui.infrium.game.chat.ChatComponent;
import ltd.bui.infrium.game.components.testing.Test;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;


public class Settlements extends JavaPlugin {

    @Getter
    public static Settlements instance;

    @Getter
    private CommandFramework commandFramework;
    @Getter
    private ArrayList<Component<Settlements>> components;

    @Getter
    private ArrayList<Handler<Settlements>> handlers;

    private Logger logger;

    @Override
    public void onLoad(){
        instance = this;
        commandFramework = new CommandFramework(this);
        if (components == null) components = new ArrayList<>();
        components.add(new ChatComponent());
        components.add(new WeaponComponent());
        components.add(new Test());

        if(handlers == null) handlers = new ArrayList<>();
    }

    @Override
    public void onEnable() {

        processHandlers(true);
        processComponents(true);
        this.commandFramework.registerHelp();



    }
    @Override
    public void onDisable(){
        try {
            Collections.reverse(components);
            Collections.reverse(handlers);
            processComponents(false);
            processHandlers(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public final <T extends Listener> T registerListener(T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }


    public void processComponents(boolean enable) {
        for (Component<Settlements> component : components) {
            try {
                if (enable) {
                    component.enable(this);
                    component.registerCommands(this);
                    component.registerListener(this);
                } else {
                    component.disable(this);
                }
                logger.info(enable ? "Enabling " + component.getClass().getSimpleName() : "Disabling " + component.getClass().getSimpleName());
            } catch (Exception e) {
                logger.info("failed to process component");
                e.printStackTrace();
            }
        }
    }

    public void processHandlers(boolean enable) {
        if (handlers.isEmpty()) return;
        for (Handler<Settlements> handler : handlers) {
            try {
                if (enable) {
                    handler.enable(this);
                } else {
                    handler.disable(this);
                }
                logger.info(enable ? "Enabling " + handler.getClass().getSimpleName() : "Disabling " + handler.getClass().getSimpleName());
            } catch (Exception e) {
                logger.info("failed to process component");
                e.printStackTrace();
            }
        }
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public static <T extends Enum<T>> T getComponent(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }




}
