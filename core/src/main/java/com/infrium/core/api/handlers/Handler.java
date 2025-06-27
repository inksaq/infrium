package com.infrium.core.api.handlers;

public abstract class Handler<T> implements IHandler<T> {

    public void enable(T plugin) {}

    public void disable(T plugin) {}
}
