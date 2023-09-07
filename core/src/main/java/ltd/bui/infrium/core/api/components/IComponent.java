package ltd.bui.infrium.core.api.components;


import ltd.bui.infrium.game.ComponentNotEnabledException;

public interface IComponent<T> {

    void enable(T plugin) throws ComponentNotEnabledException;

    void disable(T plugin);

    void registerListener(T plugin);

    void registerCommands(T plugin);
}
