package ltd.bui.infrium.api.hive.enums;

import lombok.Getter;

public enum QueueChannels {
  PLAYER_QUEUE_JOIN("infrium:hive:queue:player:join"),
  PLAYER_QUEUE_LEAVE("infrium:hive:queue:player:leave"),
  PARTY_QUEUE_JOIN("infrium:hive:queue:party:join"),
  PARTY_QUEUE_LEAVE("infrium:hive:queue:party:leave"),
  QUEUE_CONNECT("infrium:hive:queue:connect"), // connect the player to the queue
  ;

  @Getter private final String channel;

  QueueChannels(String chan) {
    this.channel = chan;
  }
}
