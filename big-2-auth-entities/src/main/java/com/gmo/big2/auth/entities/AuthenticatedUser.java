package com.gmo.big2.auth.entities;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

/**
 * An authenticated Big2 user with corresponding {@link Role}s
 *
 * @author csueiras
 */
@JsonDeserialize(builder = AuthenticatedUser.Builder.class)
public class AuthenticatedUser {
    private final User user;
    private final Set<Role> roles;

    private AuthenticatedUser(final Builder builder) {
        roles = ImmutableSet.copyOf(builder.roles);
        user = builder.user;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User getUser() {
        return user;
    }

    @JsonIgnore
    public boolean hasRole(final Role role) {
        return roles.contains(role);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, roles);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("roles", roles)
                .add("user", user)
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final AuthenticatedUser copy) {
        Builder builder = new Builder();
        builder.roles = copy.roles;
        builder.user = copy.user;
        return builder;
    }


    public static final class Builder {
        private Set<Role> roles;
        private User user;

        private Builder() {
            roles = new HashSet<>();
        }

        public Builder withRoles(final Set<Role> val) {
            roles = new HashSet<>(val);
            return this;
        }

        public Builder addRole(final Role val) {
            roles.add(val);
            return this;
        }

        public Builder withUser(final User val) {
            user = val;
            return this;
        }

        public AuthenticatedUser build() {
            requireNonNull(user, "User is required");
            requireNonNull(roles, "Roles cannot be null");
            return new AuthenticatedUser(this);
        }
    }
}
