package com.gmo.big2.api.config;

import com.gmo.big2.api.store.GameStore;
import com.gmo.big2.api.store.RedisGameStore;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.net.URI;
import java.util.Collections;

@Configuration
public class RedisStoreConfig {
    @Bean
    public GameStore gameStore(final ShardedJedisPool gameJedisDb) {
        return new RedisGameStore(gameJedisDb);
    }

    @Bean
    public ShardedJedisPool gameJedisDb() throws Exception {
        return new ShardedJedisPool(new GenericObjectPoolConfig(), Collections.singletonList(
                getRedisShardInfo(String.format("redis://%s:6379", getCacheHostName()))));
    }

    private JedisShardInfo getRedisShardInfo(final String hostName) throws Exception {
        LoggerFactory.getLogger(RedisStoreConfig.class).info("REDIS HOSTNAME: " + hostName);
        final JedisShardInfo shardInfo = new JedisShardInfo(new URI(hostName));
        shardInfo.setConnectionTimeout(5000);
        shardInfo.setSoTimeout(5000);
        return shardInfo;
    }

    private String getCacheHostName() {
        return System.getenv("REDIS_HOSTNAME");
    }
}
