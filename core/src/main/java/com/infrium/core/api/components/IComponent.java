package com.infrium.core.api.components;


public interface IComponent<T> {

    void enable(T plugin) throws ComponentNotEnabledException;

    void disable(T plugin);

    void registerListener(T plugin);

    void registerCommands(T plugin);
}
