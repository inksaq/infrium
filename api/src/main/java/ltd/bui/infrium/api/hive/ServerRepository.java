package ltd.bui.infrium.api.hive;

import ltd.bui.infrium.api.hive.enums.CloudChannels;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveAdd;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveDelete;
import ltd.bui.infrium.api.hive.pubsub.hive.RedisHiveUpdate;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.database.InfriumDB;
import ltd.bui.infrium.api.mongoserializer.MongoSerializer;
import ltd.bui.infrium.api.util.Constants;
import ltd.bui.infrium.api.util.TaskWaiter;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import org.hydev.logger.HyLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ltd.bui.infrium.api.util.LangUtils.syncListOf;

public class ServerRepository extends RedisPubSubAdapter<String, String> {

  public static final double LAGGY_TPS = 16.0d;
  private final Map<ServerType, List<Server>> servers = new ConcurrentHashMap<>();
  @Getter private final InfriumDB infriumDB;
  private final HyLogger logger = new HyLogger("ServerRepository");
  private TaskWaiter syncWaiter = new TaskWaiter(false);

  public ServerRepository(String redisUri, String mongoUri) {
    RedisURI uri = RedisURI.create(redisUri);
    this.infriumDB = new InfriumDB(uri, mongoUri);

    for (CloudChannels channels : CloudChannels.values()) {
      infriumDB.getPubSubConnectionReceiver().sync().subscribe(channels.getChannel());
    }
    this.sync();
    this.infriumDB.getPubSubConnectionReceiver().addListener(this);
  }

  private void sync() {
    syncWaiter.start();
    this.servers.clear();

    for (var st : ServerType.values()) {
      servers.put(st, syncListOf());
    }
    // fetch servers from db
    var collection = infriumDB.getMongoCollection("infrium", "hive");
    collection
        .find()
        .forEach(
            document -> {
              Server server = MongoSerializer.deserialize(document, Server.class);
              this.servers.get(server.getServerType()).add(server);
            });
    for (var st : ServerType.values()) {
      logger.log(
          "Loaded " + this.servers.get(st).size() + " " + st.name() + " servers from database.");
    }
    syncWaiter.finish();
  }

  @Override
  @Synchronized
  public void message(String channel, String message) {
    syncWaiter.await(); // wait the sync to finish
    if (channel.equals(CloudChannels.SERVER_UPDATE.getChannel())) {
      // a server, send his own information, ram usage, online players, tps, etc
      var update = Constants.get().getGson().fromJson(message, RedisHiveUpdate.class);
      var optionalServer = getByName(update.getServerName());
      if (optionalServer.isPresent()) {
        optionalServer.get().setServerStatus(update);
        this.onServerUpdate(optionalServer.get()); // call onServerUpdate event
      } else {
        logger.warning(
            "ServerRepository: Received update for unknown server " + update.getServerName());
      }
    } else if (channel.equals(CloudChannels.SERVER_DELETE.getChannel())) {
      var delete = Constants.get().getGson().fromJson(message, RedisHiveDelete.class);
      var c = getByName(delete.getServer().getName());
      if (removeServer(delete.getServer().getName())) {
        c.ifPresent(this::onServerDelete); // call onServerDelete event
      } else {
        logger.log(
            "ServerRepository: Server " + delete.getServer().getName() + " not deleted idk whys");
      }
    } else if (channel.equals(CloudChannels.SERVER_ADD.getChannel())) {
      var add = Constants.get().getGson().fromJson(message, RedisHiveAdd.class);
      servers.get(add.getServer().getServerType()).add(add.getServer());
      this.onServerAdd(add.getServer()); // call onServerAdd event
    } else if (channel.equals(CloudChannels.SYNC.getChannel())) {
      logger.log("--== Sync Request Received ==--");
      sync();
      this.onSync();
    }
  }

  public final Optional<Server> getByName(@NonNull String serverName) {
    for (var server : getServers()) {
      if (server.getName().equalsIgnoreCase(serverName)) {
        return Optional.of(server);
      }
    }
    return Optional.empty();
  }

  private boolean removeServer(@NonNull String serverName) {
    var s = getByName(serverName);
    if (s.isPresent()) {
      return removeServer(s.get());
    }
    return false;
  }

  private boolean removeServer(@NonNull Server server) {
    for (var s : servers.entrySet()) {
      for (var srv : s.getValue()) {
        if (srv.equals(server)) {
          return s.getValue().remove(server);
        }
      }
    }
    return false;
  }

  public List<Server> getLaggyServer(@NonNull ServerType type) {
    return this.servers.get(type).stream()
        .filter(s -> s.getServerStatus().getTps() < LAGGY_TPS)
        .toList();
  }

  public List<Server> getServers(@NonNull ServerType serverType) {
    return Collections.unmodifiableList(servers.get(serverType));
  }

  public List<Server> getServers() {
    List<Server> s = new ArrayList<>();
    this.servers.values().forEach(s::addAll);
    return s;
  }

  public int getServerCount() {
    return this.servers.values().stream().mapToInt(List::size).sum();
  }

  public int getServerCount(@NonNull ServerType type) {
    return this.servers.get(type).size();
  }

  public int getOnlinePlayers() {
    int count = 0;
    for (Server srv : getServers()) {
      if (srv.getServerStatus() != null) {
        count += srv.getServerStatus().getOnlinePlayers();
      }
    }
    return count;
  }

  public int getOnlinePlayers(@NonNull ServerType type) {
    int count = 0;
    for (Server srv : getServers(type)) {
      if (srv.getServerStatus() != null) {
        count += srv.getServerStatus().getOnlinePlayers();
      }
    }
    return count;
  }

  public void onServerUpdate(@NonNull Server server) {
    // this method can be re-implemented if you want to do something on server update
  }

  public void onServerDelete(@NonNull Server server) {
    // this method can be re-implemented if you want to do something on server delete
  }

  public void onServerAdd(@NonNull Server server) {
    // this method can be re-implemented if you want to do something on server add
  }

  public void onSync() {
    // this method can be re-implemented if you want to do something on sync
  }
}
