package com.gmo.big2.api.store;

import static com.gmo.big2.api.store.ObjectMapperSingleton.OBJECT_MAPPER;
import static com.gmo.big2.api.store.ObjectMapperSingleton.READER_FOR_PLAYER;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.gmo.playing.cards.Player;
import com.google.common.base.Strings;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author tedelen
 */
public class RedisPlayerStore implements PlayerStore {
    private static final String PLAYER_PREFIX = "player:";
    private static final String TOKEN_PREFIX = "token:";

    private final ShardedJedisPool jedisPlayerDb;

    public RedisPlayerStore(final ShardedJedisPool jedisPlayerDb) {
        this.jedisPlayerDb = Objects.requireNonNull(jedisPlayerDb, "Null jedis");
    }

    private static String playerKey(final UUID playerUuid) {
        return PLAYER_PREFIX + playerUuid.toString();
    }

    private static String tokenKey(final UUID tokenUuid) {
        return TOKEN_PREFIX + tokenUuid.toString();
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        try (final ShardedJedis jedis = jedisPlayerDb.getResource()) {
            return Optional.ofNullable(Strings.emptyToNull(jedis.get(playerKey(uuid)))).map(this::deserializePlayer);
        }
    }

    @Override
    public Optional<Player> getPlayerByTokenId(UUID uuid) {
        try (final ShardedJedis jedis = jedisPlayerDb.getResource()) {
            return Optional.ofNullable(Strings.emptyToNull(jedis.get(tokenKey(uuid))))
                    .map(UUID::fromString)
                    .flatMap(this::getPlayer);
        }
    }

    @Override
    public Optional<Player> findPlayer(String name) {
        return Optional.empty();
    }

    @Override
    public UUID newPlayer(Player player) {
        final Player result = getPlayer(player.getId()).orElse(findPlayer(player.getName()).orElse(player));
        final UUID token = UUID.randomUUID();
        try (final ShardedJedis jedisToken = jedisPlayerDb.getResource()) {
            jedisToken.set(tokenKey(token), result.getId().toString());
        }
        try (final ShardedJedis jedisPlayer = jedisPlayerDb.getResource()) {
            jedisPlayer.set(playerKey(player.getId()), OBJECT_MAPPER.writeValueAsString(player));
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not write player to redis", e);
        }
        return token;
    }

    private Player deserializePlayer(final String json) {
        try {
            return READER_FOR_PLAYER.readValue(json);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not deserialize something", e);
        }
    }
}
