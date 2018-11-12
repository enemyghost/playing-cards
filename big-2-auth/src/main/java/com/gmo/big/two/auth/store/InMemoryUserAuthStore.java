package com.gmo.big.two.auth.store;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.gmo.big.two.auth.entities.User;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * @author tedelen
 */
public class InMemoryUserAuthStore implements UserAuthStore {
    private final Map<String, HashCode> userPasswordMap;
    private final Map<String, User> userIdMap;

    public InMemoryUserAuthStore() {
        userPasswordMap = new HashMap<>();
        userIdMap = new HashMap<>();
    }


    @Override
    public Optional<User> authenticate(final String userName, final String password) {
        final String normalizedUserName = userName.toLowerCase();
        if (userPasswordMap.containsKey(normalizedUserName)
                && userPasswordMap.get(normalizedUserName).equals(hashPassword(password))) {
            return Optional.of(userIdMap.get(normalizedUserName));
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> registerUser(final String userName, final String password, final String displayName) {
        final String normalizedUserName = userName.toLowerCase();
        if (userIdMap.containsKey(normalizedUserName) ||
                userIdMap.values().stream().anyMatch(t -> t.getDisplayName().equalsIgnoreCase(displayName))) {
            return Optional.empty();
        }

        final User user = User.newBuilder()
                .withUserId(UUID.randomUUID())
                .withUserName(userName)
                .withDisplayName(Strings.isNullOrEmpty(displayName) ? userName : displayName)
                .build();

        userPasswordMap.put(normalizedUserName, hashPassword(password));
        userIdMap.put(normalizedUserName, user);
        return Optional.of(user);
    }

    private HashCode hashPassword(final String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
    }
}
