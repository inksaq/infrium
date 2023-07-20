package ltd.bui.infrium.api.hive.pubsub.hive;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RedisHiveBase {

  @SerializedName("name")
  protected String serverName;
}
