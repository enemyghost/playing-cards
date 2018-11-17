package com.gmo.big2.auth.entities;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.big2.auth.entities.AuthenticationSessionToken.Builder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

/**
 * Authentication Session token object
 *
 * @author malvarino
 */
@JsonDeserialize(builder = Builder.class)
public class AuthenticationSessionToken {
    private final String token;
    private final AuthenticatedUser user;

    private AuthenticationSessionToken(final Builder builder) {
        token = builder.token;
        user = builder.user;
    }

    public String getToken() {
        return token;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthenticationSessionToken that = (AuthenticationSessionToken) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, user);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("token", token)
                .add("user", user)
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final AuthenticationSessionToken copy) {
        Builder builder = new Builder();
        builder.token = copy.token;
        builder.user = copy.user;
        return builder;
    }


    public static final class Builder {
        private String token;
        private AuthenticatedUser user;

        private Builder() {
        }

        public Builder withToken(final String val) {
            token = val;
            return this;
        }

        public Builder withUser(final AuthenticatedUser val) {
            user = val;
            return this;
        }

        public AuthenticationSessionToken build() {
            requireNonNull(Strings.emptyToNull(token), "Token is required to be non-empty");
            requireNonNull(user, "The user is required");
            return new AuthenticationSessionToken(this);
        }
    }
}
