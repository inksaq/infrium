package ltd.bui.infrium.core.events;

import ltd.bui.infrium.api.hive.servers.Server;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnServerDeleteEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  @Getter private final Server server;

  public OnServerDeleteEvent(Server server) {
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
