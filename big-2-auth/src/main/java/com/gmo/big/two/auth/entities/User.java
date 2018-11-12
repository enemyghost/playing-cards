package com.gmo.big.two.auth.entities;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.big.two.auth.entities.User.Builder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Builder.class)
public class User {
    private final UUID userId;
    private final String userName;
    private final String displayName;

    private User(final Builder builder) {
        userId = builder.userId;
        userName = builder.userName;
        displayName = builder.displayName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(displayName, user.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, displayName);
    }

    public Map<String, Object> payload() {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        payload.put("displayName", displayName);
        return payload;
    }

    public static User fromPayload(final Map<String, Object> payload) {
        return User.newBuilder()
                .withUserId(UUID.fromString((String)payload.get("userId")))
                .withUserName((String)payload.get("userName"))
                .withDisplayName((String)payload.get("displayName"))
                .build();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("userName", userName)
                .add("displayName", displayName)
                .toString();
    }

    public static final class Builder {
        private UUID userId;
        private String userName;
        private String displayName;

        private Builder() {
        }

        public Builder withUserId(final UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public User build() {
            requireNonNull(userId);
            requireNonNull(Strings.emptyToNull(userName));
            requireNonNull(Strings.emptyToNull(displayName));
            return new User(this);
        }
    }
}
