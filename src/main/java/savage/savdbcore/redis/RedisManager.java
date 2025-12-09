package savage.savdbcore.redis;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import savage.savdbcore.config.DBCoreConfig;

import java.util.function.Consumer;

/**
 * Generic Redis pub/sub manager using Lettuce.
 * Provides connection management and message publishing/subscribing.
 */
public class RedisManager {
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> subConnection;
    private StatefulRedisConnection<String, String> pubConnection;
    private final Gson gson = new Gson();
    private final DBCoreConfig.RedisConfig config;
    private boolean connected = false;
    private Consumer<String> messageHandler;

    public RedisManager(DBCoreConfig.RedisConfig config) {
        this.config = config;
    }

    /**
     * Set a message handler to process incoming Redis messages.
     * @param handler Consumer that processes message JSON strings
     */
    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * Connect to Redis and subscribe to the configured channel.
     */
    public void connect() {
        if (!config.enabled) {
            return;
        }

        try {
            RedisURI.Builder uriBuilder = RedisURI.builder()
                    .withHost(config.host)
                    .withPort(config.port);

            if (config.password != null && !config.password.isEmpty()) {
                uriBuilder.withPassword(config.password.toCharArray());
            }

            RedisURI redisURI = uriBuilder.build();
            redisClient = RedisClient.create(redisURI);
            
            // Connection for subscribing (receiving messages)
            subConnection = redisClient.connectPubSub();
            subConnection.addListener(new io.lettuce.core.pubsub.RedisPubSubAdapter<String, String>() {
                @Override
                public void message(String channel, String message) {
                    if (messageHandler != null) {
                        messageHandler.accept(message);
                    }
                }
            });
            RedisPubSubCommands<String, String> subCommands = subConnection.sync();
            subCommands.subscribe(config.channel);
            
            // Separate connection for publishing (sending messages)
            pubConnection = redisClient.connect();
            
            connected = true;
            if (config.debugLogging) {
                System.out.println("[SavDBCore] Redis connected to channel: " + config.channel);
            }

        } catch (Exception e) {
            System.err.println("[SavDBCore] Failed to connect to Redis: " + e.getMessage());
            if (config.debugLogging) {
                e.printStackTrace();
            }
            connected = false;
        }
    }

    /**
     * Publish a message to the Redis channel.
     * @param message The message object to publish (will be serialized to JSON)
     */
    public void publish(Object message) {
        if (!connected || pubConnection == null) return;

        try {
            String json = gson.toJson(message);
            RedisCommands<String, String> commands = pubConnection.sync();
            commands.publish(config.channel, json);
            
            if (config.debugLogging) {
                System.out.println("[SavDBCore] Published message: " + json);
            }
        } catch (Exception e) {
            System.err.println("[SavDBCore] Failed to publish message: " + e.getMessage());
            if (config.debugLogging) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Publish a raw JSON string to the Redis channel.
     * @param json The JSON string to publish
     */
    public void publishRaw(String json) {
        if (!connected || pubConnection == null) return;

        try {
            RedisCommands<String, String> commands = pubConnection.sync();
            commands.publish(config.channel, json);
            
            if (config.debugLogging) {
                System.out.println("[SavDBCore] Published raw message: " + json);
            }
        } catch (Exception e) {
            System.err.println("[SavDBCore] Failed to publish raw message: " + e.getMessage());
            if (config.debugLogging) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shutdown the Redis connections.
     */
    public void shutdown() {
        if (subConnection != null) {
            subConnection.close();
        }
        if (pubConnection != null) {
            pubConnection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
        connected = false;
    }

    /**
     * Check if Redis is connected.
     * @return true if connected
     */
    public boolean isConnected() {
        return connected;
    }

    public Gson getGson() {
        return gson;
    }

    public DBCoreConfig.RedisConfig getConfig() {
        return config;
    }
}
