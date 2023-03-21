package ltd.bui.infrium.api.hive.pubsub.hive;

import com.google.gson.annotations.SerializedName;
import ltd.bui.infrium.api.hive.servers.Server;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RedisHiveAdd extends RedisHiveBase {

  @SerializedName("server")
  private Server server;
}
