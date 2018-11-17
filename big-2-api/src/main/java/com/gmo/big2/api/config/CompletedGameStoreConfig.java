package com.gmo.big2.api.config;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gmo.big2.api.store.ObjectMapperSingleton;
import com.gmo.big2.store.game.CompletedGameStore;
import com.gmo.big2.store.game.MySqlCompletedGameStore;

@Configuration
@Import(MySqlStoreConfig.class)
public class CompletedGameStoreConfig {
    @Bean
    public CompletedGameStore completedGameStore(final DSLContext dslContext) {
        return new MySqlCompletedGameStore(dslContext, ObjectMapperSingleton.OBJECT_MAPPER);
    }
}
