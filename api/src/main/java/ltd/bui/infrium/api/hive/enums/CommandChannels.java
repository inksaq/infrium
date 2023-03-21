package ltd.bui.infrium.api.hive.enums;

import lombok.Getter;

public enum CommandChannels {
  REDIS_COMMAND_CHANNEL_PATTERN("infrium:hive:command:*"),
  REDIS_COMMAND_CHANNEL("infrium:hive:command:"),
  ;

  @Getter private final String channel;

  CommandChannels(String chan) {
    this.channel = chan;
  }
}
