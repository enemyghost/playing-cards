package com.gmo.big2.api.store;

import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;
import com.gmo.playing.cards.Player;

/**
 * @author tedelen
 */
public class ObjectMapperSingleton {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final ObjectReader READER_FOR_GAME = OBJECT_MAPPER.readerFor(Big2Game.class);
    static final ObjectReader READER_FOR_GAME_LOBBY = OBJECT_MAPPER.readerFor(Big2GameLobby.class);
    static final ObjectReader READER_FOR_PLAYER = OBJECT_MAPPER.readerFor(Player.class);
    static final ObjectReader READER_FOR_UUID = OBJECT_MAPPER.readerFor(UUID.class);

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.registerModule(new Jdk8Module());
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false );
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true );
        OBJECT_MAPPER.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }
}
