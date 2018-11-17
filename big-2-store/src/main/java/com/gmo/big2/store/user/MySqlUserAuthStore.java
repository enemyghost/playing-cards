package com.gmo.big2.store.user;

import static com.gmo.big2.store.mysql.schema.jooq.Tables.PLAYER;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record3;

import com.gmo.big2.auth.entities.User;
import com.google.common.hash.Hashing;

/**
 * @author tedelen
 */
public class MySqlUserAuthStore implements UserAuthStore {
    private final DSLContext dslContext;

    public MySqlUserAuthStore(final DSLContext dslContext) {
        this.dslContext = Objects.requireNonNull(dslContext, "Null DSL Context");
    }

    @Override
    public Optional<User> authenticate(final String userName, final String password) {
        final Record3<UUID, String, String> userRecord =
                dslContext.select(PLAYER.PLAYER_UUID, PLAYER.EMAIL_ADDRESS, PLAYER.DISPLAY_NAME)
                        .from(PLAYER)
                        .where(PLAYER.EMAIL_ADDRESS.eq(userName)
                                .and(PLAYER.PASSWORD_HASH.eq(hashPassword(password))))
                        .fetchOne();
        return Optional.ofNullable(userRecord)
                .map(record -> User.newBuilder()
                        .withUserId(record.get(PLAYER.PLAYER_UUID))
                        .withDisplayName(record.get(PLAYER.DISPLAY_NAME))
                        .withUserName(record.get(PLAYER.EMAIL_ADDRESS))
                        .build());
    }

    @Override
    public Optional<User> registerUser(final String userName, final String password, final String displayName) {
        if (dslContext.select(PLAYER.PLAYER_UUID)
                .from(PLAYER)
                .where(PLAYER.EMAIL_ADDRESS.eq(userName).or(PLAYER.DISPLAY_NAME.eq(displayName)))
                .fetch()
                .size() > 0) {
            // User or Display name already exist
            return Optional.empty();
        }
        dslContext.insertInto(PLAYER)
                .columns(PLAYER.PLAYER_UUID, PLAYER.EMAIL_ADDRESS, PLAYER.DISPLAY_NAME, PLAYER.PASSWORD_HASH)
                .values(UUID.randomUUID(), userName, displayName, hashPassword(password))
                .execute();

        return authenticate(userName, password);
    }

    private static byte[] hashPassword(final String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).asBytes();
    }
}
