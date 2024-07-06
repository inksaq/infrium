package ltd.bui.infrium.api;

import com.mongodb.client.model.Filters;
import io.lettuce.core.RedisURI;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import ltd.bui.infrium.api.configuration.InfriumConfiguration;
import ltd.bui.infrium.api.data.InfriumPlayerData;
import ltd.bui.infrium.api.database.InfriumDB;
import ltd.bui.infrium.api.hive.ServerRepository;
import ltd.bui.infrium.api.mongoserializer.MongoSerializer;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.api.punishments.PunishmentManager;
import ltd.bui.infrium.api.util.Constants;
import ltd.bui.infrium.api.util.TaskWaiter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class InfriumProvider<T> {

    private final Map<T, AbstractInfriumPlayer<T>> infriumPlayerMap = new ConcurrentHashMap<>();
    
    private final Map<String, TaskWaiter> uuidWaiterMap = new ConcurrentHashMap<>();
    @Getter
    private final ConfigurationContainer<?> configurationContainer;

    @Getter private final InfriumDB infriumDB;
    private final Object lock = new Object();
    @Getter private final PunishmentManager punishmentManager;
    private ServerRepository repository = null;

    public InfriumProvider(@NonNull ConfigurationContainer<?> configurationContainer) throws IOException {
        this.configurationContainer = configurationContainer;

        InfriumConfiguration.setConfigurationContainer(this.configurationContainer);
//        this.configurationContainer.save( "api.yml"); //TODO Fix hardcoded and add support for config
        RedisURI redisURI = RedisURI.create(InfriumConfiguration.REDIS_URI.getString());
        redisURI.setDatabase(0);
        this.infriumDB = new InfriumDB(redisURI, InfriumConfiguration.MONGODB_URI.getString());
        getOnlinePlayers().forEach(this::getInfriumPlayerAsync);

        this.punishmentManager = new PunishmentManager(this);
    }

    public Optional<AbstractInfriumPlayer<T>> getInfriumPlayer(@NonNull T t) {
        synchronized (lock) {
            var ap = this.infriumPlayerMap.get(t);
            if (ap == null) { // fetch from the database, the player is not in the cache
                ap = craftInfriumPlayer(t);
                TaskWaiter waiter = uuidWaiterMap.get(ap.getUsername());

                if (waiter == null) {
                    waiter = new TaskWaiter();

                    uuidWaiterMap.put(ap.getUsername(), waiter);

                    var collection = this.infriumDB.getMongoCollection("infrium", "users");
                    var d = collection.find(Filters.eq("_id", ap.getUniqueId().toString())).first();

                    if (d != null) { // check if a document already exist into the collection.
                        var tempAP =
                                MongoSerializer.deserialize(d, InfriumPlayerData.class); // temp InfriumPlayer<Object>
                        ap.setPlayerData(tempAP);
                    } else { // document not present, creating it.
                        collection.insertOne(MongoSerializer.serialize(ap.getPlayerData())); // insert into db
                    }
                    ap.getPlayerData().setLastLogin(System.currentTimeMillis());

                    if (ap.isOnline()) { // fix zombie object caused by player that join and leave fast
                        this.infriumPlayerMap.put(t, ap);
                    }
                    this.uuidWaiterMap.remove(ap.getUsername());
                    waiter.finish();
                    return Optional.of(ap);
                }
                waiter.await(700L); // wait max 700ms
                return Optional.ofNullable(this.infriumPlayerMap.get(t));
            }
            return Optional.of(ap);
        }
    }

    /**
     * Return InfriumPlayer Async
     *
     * @param t return an CompletableFuture<Optional<InfriumPlayer>> given the t object
     */
    public CompletableFuture<Optional<AbstractInfriumPlayer<T>>> getInfriumPlayerAsync(@NonNull T t) {
        return CompletableFuture.supplyAsync(() -> getInfriumPlayer(t), Constants.get().getExecutor());
    }

    /**
     * @param infriumPlayer Update InfriumPlayer data in the database
     */
    public void saveInfriumPlayer(@NonNull AbstractInfriumPlayer<T> infriumPlayer) {
        var data = MongoSerializer.serialize(infriumPlayer.getPlayerData());
        var collection = this.infriumDB.getMongoCollection("infrium", "users");
        collection.findOneAndReplace(Filters.eq("_id", infriumPlayer.getUniqueId().toString()), data);
    }

    /**
     * @param infriumPlayer Update InfriumPlayer data in the database Async
     */
    public CompletableFuture<Class<Void>> saveInfriumPlayerAsync(
            @NonNull AbstractInfriumPlayer<T> infriumPlayer) {
        return CompletableFuture.supplyAsync(
                () -> {
                    saveInfriumPlayer(infriumPlayer);
                    return Void.TYPE;
                },
                Constants.get().getExecutor());
    }

    /** Get online InfriumPlayers */
    public List<Optional<AbstractInfriumPlayer<T>>> getOnlineInfriumPlayers() {
        return this.getOnlinePlayers().stream()
                .map(this::getInfriumPlayer)
                .filter(Optional::isPresent)
                .toList();
    }

    /** Get online T and InfriumPlayer TupleList */
    public List<ImmutablePair<T, AbstractInfriumPlayer<T>>> getPlayersAndInfriumPlayers() {
        List<ImmutablePair<T, AbstractInfriumPlayer<T>>> list = new ArrayList<>();
        for (var onlinePlayer : this.getOnlinePlayers()) {
            this.getInfriumPlayer(onlinePlayer)
                    .ifPresent(
                            infriumPlayerData -> list.add(new ImmutablePair<>(onlinePlayer, infriumPlayerData)));
        }
        return Collections.unmodifiableList(list);
    }

    public void onJoin(@NonNull T t) {
        // setup player data
        this.getInfriumPlayerAsync(t);
    }

    public void onQuit(@NonNull T t) {
        // save the data and quit
        this.getInfriumPlayer(t).ifPresent(this::saveInfriumPlayerAsync);
        synchronized (lock) {
            this.infriumPlayerMap.remove(t);
        }
    }


    public final void shutdown() {
        this.infriumDB.shutdown();
        Constants.get().shutdown();
    }

    @NonNull
    public ServerRepository serverRepositoryBuilder() {
        return new ServerRepository(
                InfriumConfiguration.REDIS_URI.getString(), InfriumConfiguration.MONGODB_URI.getString());
    }

    @Synchronized
    public ServerRepository getRepository() {
        if (repository == null) { // lazy init, initialized only if needed
            this.repository = serverRepositoryBuilder();
        }
        return repository;
    }


    public abstract List<T> getOnlinePlayers();

    public abstract AbstractInfriumPlayer<T> craftInfriumPlayer(@NonNull T playerObject);
    
}
