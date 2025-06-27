package com.infrium.hive.command.s;

import com.infrium.hive.Hive;
import com.infrium.hive.command.Command;
import com.infrium.api.hive.enums.ServerType;
import com.infrium.api.hive.servers.Server;

import java.util.Optional;

public class HostCommand extends Command {

  public HostCommand() {
    super("host");
  }

  @Override
  public void onCommand(String[] args) {
    if (args.length == 0) {
      getLogger().log("Not enough arguments, please specify a server type.");
    } else {
      try {
        ServerType type = ServerType.valueOf(args[0].toUpperCase());
        int qt = 1;
        if (args.length == 2) {
          qt = Integer.parseInt(args[1]);
        }
        for (int i = 0; i < qt; i++) {
          Optional<Server> server = Hive.getInstance().hostServer(type);
          if (server.isPresent()) {
            getLogger().log(i + ") Server Hosted! Name: " + server.get().getName());
          } else {
            getLogger().error("Can't host the server.");
          }
        }

      } catch (IllegalArgumentException e) {
        getLogger().error("Server type not found.");
      }
    }
  }
}
