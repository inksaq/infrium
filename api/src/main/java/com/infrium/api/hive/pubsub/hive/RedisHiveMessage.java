package com.infrium.api.hive.pubsub.hive;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RedisHiveMessage extends RedisHiveBase {

  @SerializedName("message")
  private String message; // any kind of message, json, simple string & so on
}
