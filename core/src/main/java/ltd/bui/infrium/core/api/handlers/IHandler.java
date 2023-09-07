package ltd.bui.infrium.core.api.handlers;

public interface IHandler<T> {
    void enable(T plugin);

    void disable(T plugin);

}
