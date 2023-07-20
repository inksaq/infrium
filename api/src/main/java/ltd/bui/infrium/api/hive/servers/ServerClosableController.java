package ltd.bui.infrium.api.hive.servers;

import lombok.NonNull;

public interface ServerClosableController {

  ServerClosableController DEFAULT = server -> true;

  boolean canClose(@NonNull Server server);
}
