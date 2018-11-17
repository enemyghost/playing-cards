package com.gmo.big2.api.config;

import java.time.Duration;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gmo.big.two.auth.api.JsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.impl.StaticJsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.utils.JwtUtils;
import com.gmo.big2.store.user.MySqlUserAuthStore;
import com.gmo.big2.store.user.UserAuthStore;

/**
 * Configuration for auth-related beans
 */
@Configuration
@Import(MySqlStoreConfig.class)
public class AuthConfig {
    @Bean
    public UserAuthStore userAuthStore(final DSLContext dslContext) {
        return new MySqlUserAuthStore(dslContext);
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
