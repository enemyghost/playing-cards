package com.gmo.big.two.auth.store;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmo.big.two.auth.entities.User;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author tedelen
 */
public class RedisUserAuthStore implements UserAuthStore {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String USER_PREFIX = "user:";
    private static final String USER_PW_PREFIX = "user:password:";
    private static final String DISPLAY_NAME_PREFIX = "displayName:";

    private final ShardedJedisPool jedisUserDb;

    public RedisUserAuthStore(final ShardedJedisPool jedisUserDb) {
        this.jedisUserDb = Objects.requireNonNull(jedisUserDb);
    }

    private static String userKey(final String userName) {
        return USER_PREFIX + userName;
    }

    private static String displayNameKey(final String displayName) {
        return DISPLAY_NAME_PREFIX + displayName;
    }

    private static String passwordKey(final String userName, final String password) {
        return USER_PW_PREFIX + userName + ":" + hashPassword(password);
    }

    private static HashCode hashPassword(final String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
    }

    @Override
    public Optional<User> authenticate(final String userName, final String password) {
        try (final ShardedJedis jedis = jedisUserDb.getResource()) {
            if (!jedis.exists(userKey(userName))) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(jedis
                        .get(passwordKey(userName, password)))
                        .map(this::deserializeUser);
            }
        }
    }

    @Override
    public Optional<User> registerUser(final String userName, final String password, final String displayName) {
        try (final ShardedJedis jedis = jedisUserDb.getResource()) {
            if (jedis.exists(userKey(userName)) || jedis.exists(displayNameKey(displayName))) {
                return Optional.empty();
            }

            final User user = User.newBuilder()
                    .withUserId(UUID.randomUUID())
                    .withUserName(userName)
                    .withDisplayName(displayName)
                    .build();
            jedis.set(userKey(userName), "active");
            jedis.set(displayNameKey(displayName), "active");
            jedis.set(passwordKey(userName, password), OBJECT_MAPPER.writeValueAsString(user));

            return Optional.of(user);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not write player to redis", e);
        }
    }

    private User deserializeUser(final String json) {
        try {
            return OBJECT_MAPPER.readValue(json, User.class);
        } catch (final IOException e) {
            throw new UncheckedIOException("Could not deserialize user", e);
        }
    }
}
