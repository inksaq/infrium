package com.infrium.core.events;

import com.infrium.api.hive.servers.Server;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnServerUpdateEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter private final Server server;

  public OnServerUpdateEvent(Server server) {
    this.server = server;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return getHandlerList();
  }
}
