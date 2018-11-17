package com.gmo.big2.auth.entities;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.big2.auth.entities.AuthenticationRequest.Builder;
import com.google.common.base.MoreObjects;

/**
 * Authentication Request object
 *
 * @author malvarino
 */
@JsonDeserialize(builder = Builder.class)
public class AuthenticationRequest {
    private final String userName;
    private final String password;

    private AuthenticationRequest(final Builder builder) {
        userName = builder.userName;
        password = builder.password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userName", userName)
                .add("password", "XXXXXXXX")
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String userName;
        private String password;

        private Builder() {
        }

        public Builder withUserName(final String val) {
            userName = val;
            return this;
        }

        public Builder withPassword(final String val) {
            password = val;
            return this;
        }

        public AuthenticationRequest build() {
            requireNonNull(emptyToNull(userName), "userName is empty");
            requireNonNull(emptyToNull(password), "password is empty");
            return new AuthenticationRequest(this);
        }
    }
}
