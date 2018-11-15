package com.gmo.big2.api.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gmo.big.two.auth.api.JsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.impl.StaticJsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.store.InMemoryUserAuthStore;
import com.gmo.big.two.auth.store.RedisUserAuthStore;
import com.gmo.big.two.auth.store.UserAuthStore;
import com.gmo.big.two.auth.utils.JwtUtils;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Configuration for auth-related beans
 */
@Configuration
@Import(RedisStoreConfig.class)
public class AuthConfig {
    @Bean
    public UserAuthStore userAuthStore(final ShardedJedisPool gameJedisDb) {
        return new RedisUserAuthStore(gameJedisDb);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(Duration.ofHours(96));
    }
    
    @Bean
    public JsonWebTokenAuthenticationProvider jsonWebTokenAuthenticationProvider(final JwtUtils jwtUtils,
                                                                                    final UserAuthStore userAuthStore) {
        return new StaticJsonWebTokenAuthenticationProvider(jwtUtils, userAuthStore);
    }
}
