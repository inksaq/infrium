package ltd.bui.infrium.hive;

import static ltd.bui.infrium.api.hive.ServerRepository.LAGGY_TPS;

import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.hive.enums.QueueChannels;
import ltd.bui.infrium.api.hive.enums.QueueLeftReason;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.hive.pubsub.queue.RedisQueueConnect;
import ltd.bui.infrium.api.hive.queue.QueueRepository;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.util.Constants;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.hydev.logger.HyLogger;

public class QueueManager extends QueueRepository {

  private final HyLogger logger = new HyLogger("QueueManager");

  public QueueManager(ServerRepository serverRepository) {
    super(serverRepository);
    Constants.get().getExecutor().scheduleAtFixedRate(this::logic, 0, 3, TimeUnit.SECONDS);
  }

  private void logic() {
    for (ServerType serverType : ServerType.values()) {
      var servers = new ArrayList<>(this.repository.getServers(serverType));
      for (Server server : servers) { // loop through all servers selected by type
        if (server.getServerStatus().getTps() >= LAGGY_TPS
            && server.getPinger().ping()) { // join non laggy server & reachable server
          int onlineCounter =
              server.getServerStatus().getOnlinePlayers(); // fix overflow players count
          String username;
          while ((username =
                      this.db.getRedisConnection().sync().srandmember(serverType.getQueueName()))
                  != null
              && onlineCounter < server.getServerType().getMaxPlayers()
              && server.getServerType().canJoin(server)) { // join until queue is empty
            var connectRequest = new RedisQueueConnect();
            connectRequest.setPlayerName(username);
            connectRequest.setServerName(server.getName());
            connectRequest.setServerType(server.getServerType());
            this.getRepository()
                .getInfriumDB()
                .publishJson(QueueChannels.QUEUE_CONNECT.getChannel(), connectRequest);
            this.leaveQueue(
                username,
                QueueLeftReason
                    .CONNECTED); // player has been processed, we can remove it from the queue
            onlineCounter++;
            logger.log("Connected " + username + " to " + server.getName());
            server.getServerStatus().setOnlinePlayers(onlineCounter);
          }
        }
      }
    }
  }
}
