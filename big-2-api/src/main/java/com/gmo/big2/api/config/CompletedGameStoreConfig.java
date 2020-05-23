package com.gmo.big2.api.config;

import com.gmo.big2.store.game.CompletedGameStore;
import com.gmo.big2.store.game.MySqlCompletedGameStore;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MySqlStoreConfig.class)
public class CompletedGameStoreConfig {
    @Bean
    public CompletedGameStore completedGameStore(final DSLContext dslContext) {
        return new MySqlCompletedGameStore(dslContext);
    }
}
