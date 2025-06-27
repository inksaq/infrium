package com.infrium.api.hive.pubsub.hive;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RedisHiveUpdate extends RedisHiveBase {

  @SerializedName("ramUsage")
  private long ramUsage;

  @SerializedName("tps")
  private double tps;

  @SerializedName("onlinePlayers")
  private int onlinePlayers;

  @SerializedName("motd")
  private String motd;
}
