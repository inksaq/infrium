package ltd.bui.infrium.api.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.NonNull;
import ltd.bui.infrium.api.util.Constants;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.concurrent.CompletableFuture;
@Getter
public class InfriumDB {
    private final MongoClient mongoClient;
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> redisConnection;
    private final StatefulRedisPubSubConnection<String, String>
            pubSubConnectionReceiver; // receive message
    private final StatefulRedisPubSubConnection<String, String>
            pubSubConnectionSender; // send message
    private final RedisURI redisUri;

    public InfriumDB(RedisURI redisUri, @NonNull final String mongoUri, String... channels) {
        this.redisUri = redisUri;
        this.redisClient = RedisClient.create(redisUri); // "redis://127.0.0.1:6379/0"

        CodecRegistry pojoCodecRegistry =
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());

        CodecRegistry codecRegistry =
                CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings clientSettings =
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .codecRegistry(codecRegistry)
                        .build();

        this.mongoClient = MongoClients.create(clientSettings); //
        this.redisConnection = redisClient.connect();
        this.pubSubConnectionReceiver = redisClient.connectPubSub();
        this.pubSubConnectionSender = redisClient.connectPubSub();
        if (channels.length > 0) {
            this.pubSubConnectionReceiver.sync().subscribe(channels); // Subscribe channels here
        }
    }

    public InfriumDB(
            @NonNull final String redisUri, @NonNull final String mongoUri, String... channels) {
        this(RedisURI.create(redisUri), mongoUri, channels);
    }

    public MongoDatabase getMongoDatabase(String name) {
        return this.mongoClient.getDatabase(name);
    }

    public MongoCollection<Document> getMongoCollection(String db, String name) {
        return this.mongoClient.getDatabase(db).getCollection(name);
    }

    public <T> MongoCollection<T> getMongoCollection(String db, String name, Class<T> t) {
        return this.mongoClient.getDatabase(db).getCollection(name, t);
    }

    public Long publishMessage(String channel, String message) {
        return this.pubSubConnectionSender.sync().publish(channel, message);
    }

    public Long publishJson(String channel, @NonNull Object oj) {
        return this.pubSubConnectionSender
                .sync()
                .publish(channel, Constants.get().getGson().toJson(oj));
    }

    public RedisFuture<Long> publishMessageAsync(String channel, String message) {
        return this.pubSubConnectionSender.async().publish(channel, message);
    }

    public RedisFuture<Long> publishJsonAsync(String channel, @NonNull Object obj) {
        return this.pubSubConnectionSender
                .async()
                .publish(channel, Constants.get().getGson().toJson(obj));
    }

    public void registerRedisPubSubListener(RedisPubSubListener<String, String> listener) {
        this.pubSubConnectionReceiver.addListener(listener);
    }

    // Redis data handling

    /** Redis set String value sync */
    public String redisSet(String key, String value) {
        return this.redisConnection.sync().set(key, value);
    }

    /** Redis set JSON (object will be converted to json using GoogleGson) value sync */
    public String redisSetJson(String key, Object value) {
        return this.redisSet(key, Constants.get().getGson().toJson(value));
    }

    /** Redis get string sync */
    public String redisGet(String key) {
        return this.redisConnection.sync().get(key);
    }

    /** Redis get Json sync - */
    public <T> T redisGetJson(String key, Class<T> clazz) {
        return Constants.get().getGson().fromJson(this.redisGet(key), clazz);
    }

    /** Redis set value async - */
    public RedisFuture<String> redisSetAsync(String key, String value) {
        return this.redisConnection.async().set(key, value);
    }

    /** Redis set JSON (object will be converted to json using GoogleGson) value async */
    public RedisFuture<String> redisSetJsonAsync(String key, Object value) {
        return this.redisSetAsync(key, Constants.get().getGson().toJson(value));
    }

    /** Redis get String async */
    public RedisFuture<String> redisGetAsync(String key) {
        return this.redisConnection.async().get(key);
    }

    /** Redis get Json async - */
    public <T> CompletableFuture<T> redisGetJsonAsync(String key, Class<T> clazz) {
        return this.redisGetAsync(key)
                .thenApplyAsync(x -> Constants.get().getGson().fromJson(x, clazz))
                .toCompletableFuture();
    }

    public final void shutdown() {
        this.redisConnection.close();
        this.pubSubConnectionReceiver.close();
        this.pubSubConnectionSender.close();
        this.redisClient.shutdown();
    }

    public long redisExists(String key) {
        return this.redisConnection.sync().exists(key);
    }

    public RedisFuture<Long> redisExistsAsync(String key) {
        return this.redisConnection.async().exists(key);
    }

    public long redisAppend(String key, String toAppend) {
        return this.redisConnection.sync().append(key, toAppend);
    }

    public RedisFuture<Long> redisAppendAsync(String key, String toAppend) {
        return this.redisConnection.async().append(key, toAppend);
    }
}
