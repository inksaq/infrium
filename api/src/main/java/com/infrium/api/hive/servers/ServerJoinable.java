package com.infrium.api.hive.servers;

import lombok.NonNull;

public interface ServerJoinable {

  ServerJoinable DEFAULT =
      server ->
          server.getServerStatus() != null
              && server.getServerStatus().getOnlinePlayers()
                  < server.getServerType().getMaxPlayers();

  boolean isJoinable(@NonNull Server server);
}
