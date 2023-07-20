package ltd.bui.infrium.api.hive.pubsub.queue;

import com.google.gson.annotations.SerializedName;
import ltd.bui.infrium.api.hive.enums.ServerType;
import lombok.Data;

@Data
public class RedisQueueConnect {

  @SerializedName("PlayerName")
  private String playerName;

  @SerializedName("serverType")
  private ServerType serverType;

  @SerializedName("serverName")
  private String serverName;
}
