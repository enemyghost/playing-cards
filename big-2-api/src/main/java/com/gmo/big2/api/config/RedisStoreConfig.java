package com.gmo.big2.api.config;

import java.net.URI;
import java.util.Collections;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gmo.big2.api.store.GameStore;
import com.gmo.big2.api.store.PlayerStore;
import com.gmo.big2.api.store.RedisGameStore;
import com.gmo.big2.api.store.RedisPlayerStore;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

@Configuration
public class RedisStoreConfig {
    @Bean
    public GameStore gameStore(final ShardedJedisPool gameJedisDb) {
        return new RedisGameStore(gameJedisDb);
    }

    @Bean
    public PlayerStore playerStore(final ShardedJedisPool gameJedisDb) {
        return new RedisPlayerStore(gameJedisDb);
    }

    @Bean
    public ShardedJedisPool gameJedisDb() throws Exception {
        return new ShardedJedisPool(new GenericObjectPoolConfig(), Collections.singletonList(
                getRedisShardInfo(String.format("rediss://%s:6380", getCacheHostName()))));
    }

    @Bean
    public ShardedJedisPool gameLobbyJedisDb() throws Exception {
        return new ShardedJedisPool(new GenericObjectPoolConfig(), Collections.singletonList(
                getRedisShardInfo(String.format("rediss://%s:6380/%d", getCacheHostName(), 2))));
    }

    @Bean
    public ShardedJedisPool playerJedisDb() throws Exception {
        return new ShardedJedisPool(new GenericObjectPoolConfig(), Collections.singletonList(
                getRedisShardInfo(String.format("rediss://%s:6380/%d", getCacheHostName(), 3))));
    }

    @Bean
    public ShardedJedisPool playerTokenJedisDb() throws Exception {
        return new ShardedJedisPool(new GenericObjectPoolConfig(), Collections.singletonList(
                getRedisShardInfo(String.format("rediss://%s:6380/%d", getCacheHostName(), 4))));
    }

    private JedisShardInfo getRedisShardInfo(final String hostName) throws Exception {
        // Connect to the Redis cache over the SSL port using the key.
        LoggerFactory.getLogger(RedisStoreConfig.class).info("REDIS HOSTNAME: " + hostName);
        final JedisShardInfo shardInfo = new JedisShardInfo(new URI(hostName));
        shardInfo.setPassword(getCacheKey()); /* Use your access key. */
        shardInfo.setConnectionTimeout(5000);
        shardInfo.setSoTimeout(5000);
        return shardInfo;
    }

    private String getCacheHostName() {
        return System.getenv("REDISCACHEHOSTNAME");
    }

    private String getCacheKey() {
        return System.getenv("REDISCACHEKEY");
    }
}
