package com.gmo.big2.api.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gmo.big.two.auth.api.JsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.impl.StaticJsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.store.InMemoryUserAuthStore;
import com.gmo.big.two.auth.store.UserAuthStore;
import com.gmo.big.two.auth.utils.JwtUtils;

/**
 * Configuration for auth-related beans
 */
@Configuration
public class AuthConfig {
    @Bean
    public UserAuthStore userAuthStore() {
        return new InMemoryUserAuthStore();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(Duration.ofMinutes(30L));
    }
    
    @Bean
    public JsonWebTokenAuthenticationProvider jsonWebTokenAuthenticationProvider(final JwtUtils jwtUtils,
                                                                                    final UserAuthStore userAuthStore) {
        return new StaticJsonWebTokenAuthenticationProvider(jwtUtils, userAuthStore);
    }
}
