package com.infrium.hive.command.s;

import com.infrium.api.hive.servers.Server;
import com.infrium.hive.Hive;
import com.infrium.hive.command.Command;

import java.util.Optional;

public class AddDevCommand extends Command {

  public AddDevCommand() {
    super("dev");
  }

  @Override
  public void onCommand(String[] args) {
    if (args.length == 0 || args.length >= 4) {
      getLogger().log("Not enough arguments, please specify a server type.");
      getLogger().log("/dev <ip> <port> <name>");
    } else {
      try {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];

          Optional<Server> server = Hive.getInstance().hostRDev(ip, port, name);
          if (server.isPresent()) {
            getLogger().log(" Server Hosted! Name: " + server.get().getName());
          } else {
            getLogger().error("Can't host the server.");
          }


      } catch (IllegalArgumentException e) {
        getLogger().error("Server type not found.");
      }
    }
  }
}
