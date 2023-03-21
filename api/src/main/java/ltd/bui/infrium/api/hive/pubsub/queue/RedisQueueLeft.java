package ltd.bui.infrium.api.hive.pubsub.queue;

import com.google.gson.annotations.SerializedName;
import ltd.bui.infrium.api.hive.enums.QueueLeftReason;
import lombok.Data;

@Data
public class RedisQueueLeft {

  @SerializedName("PlayerName")
  private String playerName;

  @SerializedName("reason")
  private QueueLeftReason reason;
}
