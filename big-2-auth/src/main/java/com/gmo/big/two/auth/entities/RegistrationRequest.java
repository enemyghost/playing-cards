package com.gmo.big.two.auth.entities;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.big.two.auth.entities.RegistrationRequest.Builder;
import com.google.common.base.MoreObjects;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Builder.class)
public class RegistrationRequest {
    private final String userName;
    private final String password;
    private final String displayName;

    private RegistrationRequest(final Builder builder) {
        userName = builder.userName;
        password = builder.password;
        displayName = builder.displayName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userName", userName)
                .add("password", password)
                .add("displayName", displayName)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password) &&
                Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, displayName);
    }

    public static final class Builder {
        private String userName;
        private String password;
        private String displayName;

        private Builder() {
        }

        public Builder withUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder withDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public RegistrationRequest build() {
            requireNonNull(emptyToNull(userName), "user is empty");
            requireNonNull(emptyToNull(password), "password is empty");
            requireNonNull(emptyToNull(displayName), "display name is empty");
            return new RegistrationRequest(this);
        }
    }
}
