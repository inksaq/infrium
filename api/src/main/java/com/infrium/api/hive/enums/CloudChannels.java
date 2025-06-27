package com.infrium.api.hive.enums;

import lombok.Getter;

public enum CloudChannels {
  SERVER_ADD("infrium:hive:server:add"),
  SERVER_DELETE("infrium:hive:server:delete"),
  SERVER_UPDATE("infrium:hive:server:update"),
  SERVER_SHUTDOWN("infrium:hive:server:shutdown"),
  MESSAGE("infrium:hive:message"),
  CONNECT("infrium:hive:server:connect"),
  SYNC("infrium:hive:sync"),
  ;

  @Getter private final String channel;

  CloudChannels(String chan) {
    this.channel = chan;
  }
}
