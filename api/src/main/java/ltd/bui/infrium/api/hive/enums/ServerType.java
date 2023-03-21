package ltd.bui.infrium.api.hive.enums;

import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.hive.servers.ServerClosableController;
import ltd.bui.infrium.api.hive.servers.ServerJoinable;
import lombok.Getter;
import lombok.NonNull;

public enum ServerType {
  LOBBY("lobby.zip", 60, 256, 512, false),
  CAKEWARS_DUOS("cakewars_duos.zip", 15, 512, 1024, false),
  CAKEWARS_SQUAD("cakewars_squad.zip", 20, 512, 1024, false),
  ;

  @Getter private final int maxRam;
  @Getter private final String zipFile;
  @Getter private final int maxPlayers;
  @Getter private final int minRam;
  @Getter private final boolean isPersistent;
  private final ServerClosableController controller;
  private final ServerJoinable joinable;

  ServerType(
      String zipFile,
      int maxPlayers,
      int minRam,
      int maxRam,
      boolean isPersistent,
      ServerJoinable joinable) {
    this(
        zipFile,
        maxPlayers,
        minRam,
        maxRam,
        isPersistent,
        ServerClosableController.DEFAULT,
        joinable);
  }

  ServerType(String zipFile, int maxPlayers, int minRam, int maxRam, boolean isPersistent) {
    this(
        zipFile,
        maxPlayers,
        minRam,
        maxRam,
        isPersistent,
        ServerClosableController.DEFAULT,
        ServerJoinable.DEFAULT);
  }

  ServerType(
      String zipFile,
      int maxPlayers,
      int minRam,
      int maxRam,
      boolean isPersistent,
      ServerClosableController controller) {
    this(zipFile, maxPlayers, minRam, maxRam, isPersistent, controller, ServerJoinable.DEFAULT);
  }

  ServerType(
      String zipFile,
      int maxPlayers,
      int minRam,
      int maxRam,
      boolean isPersistent,
      ServerClosableController controller,
      ServerJoinable joinable) {
    this.zipFile = zipFile;
    this.maxPlayers = maxPlayers;
    this.minRam = minRam;
    this.maxRam = maxRam;
    this.isPersistent = isPersistent;
    this.controller = controller;
    this.joinable = joinable;
  }

  public Server createServer(String name, String host, int port) {
    Server server = new Server();
    server.setServerType(this);
    server.setIp(host);
    server.setPort(port);
    server.setName(name);
    return server;
  }

  public boolean canClose(@NonNull Server server) {
    if (!server.getServerType().equals(this)) {
      throw new IllegalArgumentException("Server type does not match");
    }
    return this.controller.canClose(server);
  }

  public boolean canJoin(@NonNull Server server) {
    return ServerJoinable.DEFAULT.isJoinable(server) && this.joinable.isJoinable(server);
  }

  public String getQueueName() {
    return String.format("queue_%s", this.name().toLowerCase());
  }
}
