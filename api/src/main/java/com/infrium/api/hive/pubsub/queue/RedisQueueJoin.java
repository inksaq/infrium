package com.infrium.api.hive.pubsub.queue;

import com.google.gson.annotations.SerializedName;
import com.infrium.api.hive.enums.ServerType;
import lombok.Data;

@Data
public class RedisQueueJoin {

  @SerializedName("PlayerName")
  private String playerName;

  @SerializedName("serverType")
  private ServerType serverType;
}
