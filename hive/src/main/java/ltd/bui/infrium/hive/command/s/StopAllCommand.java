package ltd.bui.infrium.hive.command.s;

import ltd.bui.infrium.hive.Hive;
import ltd.bui.infrium.hive.command.Command;
import ltd.bui.infrium.api.hive.enums.ServerType;

import java.util.ArrayList;

public class StopAllCommand extends Command {

  public StopAllCommand() {
    super("stopall");
  }

  @Override
  public void onCommand(String[] args) {
    if (args.length == 0) {
      getLogger().log("Not enough arguments, please specify a server type.");
    } else {
      var forced = false;
      if (args.length > 1) {
        forced = args[1].equalsIgnoreCase("true");
      }
      try {
        ServerType type = ServerType.valueOf(args[0].toUpperCase());
        int counter = 0;
        var servers = new ArrayList<>(Hive.getInstance().getRepository().getServers(type));
        for (var server : servers) {
          if (forced) {
            Hive.getInstance().forceKill(server);
          } else {
            Hive.getInstance().graciouslyKill(server);
          }
          counter++;
        }
        getLogger()
            .log(
                "Killed ("
                    + counter
                    + ") Servers. Category <"
                    + type.name()
                    + ">.   FORCED: "
                    + forced);
      } catch (IllegalArgumentException e) {
        getLogger().error("Server type not found.");
      }
    }
  }
}
