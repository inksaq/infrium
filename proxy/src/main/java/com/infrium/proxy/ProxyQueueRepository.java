package com.infrium.proxy;

import com.infrium.api.hive.ServerRepository;
import com.infrium.api.hive.enums.ServerType;
import com.infrium.api.hive.pubsub.queue.RedisQueueConnect;
import com.infrium.api.hive.pubsub.queue.RedisQueueJoin;
import com.infrium.api.hive.pubsub.queue.RedisQueueLeft;
import com.infrium.api.hive.queue.QueueRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.infrium.proxy.Proxy.get;
import static com.infrium.proxy.Proxy.serialize;

public class ProxyQueueRepository extends QueueRepository {

  private final Map<ServerType, AtomicLong> queuesSize = new ConcurrentHashMap<>();
  private final Map<String, ServerType> playerQueue = new ConcurrentHashMap<>();

  public ProxyQueueRepository(ServerRepository repository) {
    super(repository);
    for (ServerType serverType : ServerType.values()) {
      this.queuesSize.put(serverType, new AtomicLong(0L));
    }

    // implement actionbar task that send the current queue status to the players that are in a
    // queue
    Proxy.get()
        .getServer()
        .getScheduler()
        .buildTask(
            Proxy.get(),
            () -> {
              for (var set : this.playerQueue.entrySet()) {
                Proxy.get()
                    .getServer()
                    .getPlayer(set.getKey())
                    .ifPresent(
                        player -> {
                          var cmp =
                              serialize.apply(
                                  "&e&lYou are in the &6"
                                      + set.getValue().name()
                                      + " &e&lqueue. &e&l(&6"
                                      + this.queuesSize.get(set.getValue()).get()
                                      + "&e&l).");
                          player.sendActionBar(cmp);
                        });
              }
            })
        .delay(1, TimeUnit.SECONDS)
        .schedule();
  }

  @Override
  public void onConnect(RedisQueueConnect queueConnect) {
    var optionalServer = Proxy.get().getServer().getServer(queueConnect.getServerName());
    var optionalQueue = Proxy.get().getQueueLimboHandler(queueConnect.getPlayerName());
    var optionalPlayer = Proxy.get().getServer().getPlayer(queueConnect.getPlayerName());
    if (optionalServer.isPresent()) { // if server exist
      if (optionalQueue.isPresent()) { // if the player is in the limbo
        Proxy.get()
            .getQueuedJoin()
            .put(
                queueConnect.getPlayerName(),
                optionalServer.get()); // limbo players are connected in a different way

        optionalQueue.get().getPlayer().disconnect();
      } else if (optionalPlayer.isPresent()) { // else just send the player
        Proxy.get()
            .getServer()
            .getScheduler()
            .buildTask(
                Proxy.get(),
                () ->
                    optionalPlayer
                        .get()
                        .createConnectionRequest(optionalServer.get())
                        .connectWithIndication().obtrudeException(new Exception(new Exception("Not working"))))
            .schedule();
      }
    }
  }

  @Override
  public void onJoinQueue(RedisQueueJoin queueJoin) {
    System.out.println(playerQueue.size());
    System.out.println(get().getLimboPlayers().entrySet());
    Proxy.get()
        .getServer()
        .getPlayer(queueJoin.getPlayerName())
        .ifPresent(
            player -> { // if player exist
              // add task to the map
              this.playerQueue.put(queueJoin.getPlayerName(), queueJoin.getServerType());

              // update the queue size
              Proxy.get()
                  .getQueueRepository()
                  .getRepository()
                  .getInfriumDB()
                  .getRedisConnection()
                  .async()
                  .scard(queueJoin.getServerType().getQueueName())
                  .thenAccept(
                      size ->
                          ProxyQueueRepository.this
                              .queuesSize
                              .get(queueJoin.getServerType())
                              .set(size) // update queue size
                      );
            });
  }

  @Override
  public void onQueueLeft(RedisQueueLeft queueLeft) {
    ServerType oldQueue = null;
    for (var set : this.playerQueue.entrySet()) { // trying
      if (set.getKey().equalsIgnoreCase(queueLeft.getPlayerName())) {
        oldQueue = set.getValue();
        this.playerQueue.remove(set.getKey());
        break;
      }
    }
    if (oldQueue != null) {
      final ServerType finalOldQueue = oldQueue;
      Proxy.get()
          .getQueueRepository()
          .getRepository()
          .getInfriumDB()
          .getRedisConnection()
          .async()
          .scard(oldQueue.getQueueName())
          .thenAccept(
              size ->
                  ProxyQueueRepository.this
                      .queuesSize
                      .get(finalOldQueue)
                      .set(size) // update queue size
              );
    }
  }
}
