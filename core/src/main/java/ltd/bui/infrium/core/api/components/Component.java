package ltd.bui.infrium.core.api.components;

public abstract class Component<T> implements IComponent<T> {


    public void enable(T plugin) throws ComponentNotEnabledException {}

    public void disable(T plugin) {}

    public void registerListener(T plugin) {}

    public void registerCommands(T plugin) {}
}
