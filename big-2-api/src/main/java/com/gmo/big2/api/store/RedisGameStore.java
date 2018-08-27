package com.gmo.big2.api.store;

import static com.gmo.big2.api.store.ObjectMapperSingleton.OBJECT_MAPPER;
import static com.gmo.big2.api.store.ObjectMapperSingleton.READER_FOR_GAME;
import static com.gmo.big2.api.store.ObjectMapperSingleton.READER_FOR_GAME_LOBBY;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;
import com.google.common.base.Strings;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author tedelen
 */
public class RedisGameStore implements GameStore {
    private static final int TTL_SECONDS = (int)TimeUnit.DAYS.toSeconds(1);
    private static final String LOBBY_PREFIX = "lobby:";
    private static final String GAME_PREFIX = "game:";
    private final ShardedJedisPool jedisGameDb;

    public RedisGameStore(final ShardedJedisPool jedisGameDb) {
        this.jedisGameDb = Objects.requireNonNull(jedisGameDb, "Null jedis");
    }

    private static String gameKey(final UUID gameUuid) {
        return GAME_PREFIX + gameUuid.toString();
    }

    private static String lobbyKey(final UUID gameLobbyUuid) {
        return LOBBY_PREFIX + gameLobbyUuid.toString();
    }

    @Override
    public Optional<Big2Game> getGame(final UUID uuid) {
        try (final ShardedJedis jedis = jedisGameDb.getResource()) {
            return Optional.ofNullable(Strings.emptyToNull(jedis.get(gameKey(uuid)))).map(this::deserializeGame);
        }
    }

    @Override
    public Big2Game updateGame(final Big2Game game) {
        try (final ShardedJedis jedis = jedisGameDb.getResource()) {
            jedis.set(gameKey(game.getId()), OBJECT_MAPPER.writeValueAsString(game));
            jedis.expire(gameKey(game.getId()), TTL_SECONDS);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not write game state", e);
        }

        return game;
    }

    @Override
    public Optional<Big2GameLobby> getGameLobby(final UUID uuid) {
        try (final ShardedJedis jedis = jedisGameDb.getResource()) {
            return Optional.ofNullable(Strings.emptyToNull(jedis.get(lobbyKey(uuid)))).flatMap(this::deserializeGameLobby);
        }
    }

    @Override
    public Big2GameLobby newLobby() {
        final Big2GameLobby lobby = Big2GameLobby.newBuilder().build();
        updateGameLobby(lobby);
        return lobby;
    }

    @Override
    public Big2GameLobby updateGameLobby(final Big2GameLobby lobby) {
        try (final ShardedJedis jedis = jedisGameDb.getResource()) {
            jedis.set(lobbyKey(lobby.getGameId()), OBJECT_MAPPER.writeValueAsString(lobby));
            jedis.expire(lobbyKey(lobby.getGameId()), TTL_SECONDS);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not write game lobby state", e);
        }
        return lobby;
    }

    @Override
    public Big2Game startGame(final UUID uuid) {
        final Optional<Big2GameLobby> gameLobbyOpt = getGameLobby(uuid);
        if (gameLobbyOpt.isPresent()) {
            final Big2GameLobby lobby = gameLobbyOpt.get();
            final Big2Game started = lobby.start();
            updateGame(started);
            try (final ShardedJedis jedis = jedisGameDb.getResource()) {
                jedis.del(lobbyKey(uuid));
            }
            return started;
        }

        throw new RuntimeException("That game cannot be started " + uuid.toString());
    }

    private Big2Game deserializeGame(final String json) {
        try {
            return READER_FOR_GAME.readValue(json);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not deserialize something", e);
        }
    }

    private Optional<Big2GameLobby> deserializeGameLobby(final String json) {
        try {
            return Optional.of(READER_FOR_GAME_LOBBY.readValue(json));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }
}
