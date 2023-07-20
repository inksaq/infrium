package ltd.bui.infrium.hive.command.s;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.hive.command.Command;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.hive.servers.Server;

public class InfoCommand extends Command {

  public InfoCommand() {
    super("info");
  }

  @Override
  public void onCommand(String[] args) {
    StringBuilder builder = new StringBuilder();

    if (args.length == 0) {
      builder.append("ServerType | Online Players | Online Servers");
      builder.append("\n");
      for (ServerType s : ServerType.values()) {
        builder
            .append("\n")
            .append(s.name())
            .append(" | ")
            .append(Hive.getInstance().getRepository().getOnlinePlayers())
            .append(" | ")
            .append(Hive.getInstance().getRepository().getServerCount(s));
      }
    } else {
      ServerType s = ServerType.valueOf(args[0].toUpperCase());
      if (s != null) {
        builder.append("ServerName | Online Players | TPS | ramUsage | maxRam | MS TO PING");
        for (Server server : Hive.getInstance().getRepository().getServers(s)) {
          builder
              .append("\n")
              .append(server.getName())
              .append(" | ")
              .append(server.getServerStatus().getOnlinePlayers())
              .append(" | ")
              .append(Math.round(server.getServerStatus().getTps()));
          builder
              .append("tps | ")
              .append(server.getServerStatus().getRamUsage())
              .append("mb | ")
              .append(server.getServerType().getMaxRam());
          var start = System.currentTimeMillis();
          server.getPinger().ping();
          builder.append("mb | ").append(System.currentTimeMillis() - start).append("MS");
        }
        builder
            .append("\n\nServer Count (")
            .append(Hive.getInstance().getRepository().getServerCount(s))
            .append(").");
      } else {
        builder.append("\nServer type not found.");
      }
    }
    builder
        .append("\n\nTotal Server Count (")
        .append(Hive.getInstance().getRepository().getServerCount())
        .append(").");
    builder
        .append("\nTotal Players Online (")
        .append(Hive.getInstance().getRepository().getOnlinePlayers())
        .append(").");
    getLogger().log(builder);
  }
}
