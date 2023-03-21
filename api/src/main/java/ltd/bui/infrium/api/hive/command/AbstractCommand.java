package ltd.bui.infrium.api.hive.command;

import org.apache.commons.lang3.ArrayUtils;

public abstract class AbstractCommand {

  private final String[] servers;

  public AbstractCommand(String... servers) {
    this.servers = servers;
  }

  public boolean isTarget(String serverName) {
    return this.servers == null
        || this.servers.length == 0
        || ArrayUtils.contains(this.servers, serverName);
  }
}
