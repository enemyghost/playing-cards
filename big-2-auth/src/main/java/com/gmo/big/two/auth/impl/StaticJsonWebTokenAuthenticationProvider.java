package com.gmo.big.two.auth.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.gmo.big.two.auth.api.JsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.entities.AuthenticatedUser;
import com.gmo.big.two.auth.entities.AuthenticationRequest;
import com.gmo.big.two.auth.entities.AuthenticationSessionToken;
import com.gmo.big.two.auth.entities.RegistrationRequest;
import com.gmo.big.two.auth.entities.Role;
import com.gmo.big.two.auth.entities.User;
import com.gmo.big.two.auth.store.UserAuthStore;
import com.gmo.big.two.auth.utils.JwtUtils;

/**
 * {@link JsonWebTokenAuthenticationProvider} implementation that is hard coded to an admin user and a normal user, this
 * should only be used for testing purposes!
 */
public class StaticJsonWebTokenAuthenticationProvider implements JsonWebTokenAuthenticationProvider {
    private final JwtUtils jwtUtils;
    private final UserAuthStore userAuthStore;

    public StaticJsonWebTokenAuthenticationProvider(final JwtUtils jwtUtils, final UserAuthStore userAuthStore) {
        this.jwtUtils = requireNonNull(jwtUtils, "Null utils");
        this.userAuthStore = requireNonNull(userAuthStore, "Null auth store");
    }

    @Override
    public AuthenticationSessionToken authenticateUser(final AuthenticationRequest authenticationRequest) {
        requireNonNull(authenticationRequest, "Null authentication request");

        final String userName = authenticationRequest.getUserName();
        final String pass = authenticationRequest.getPassword();
        final Optional<User> user = userAuthStore.authenticate(userName, pass);
        if (user.isPresent()) {
            final AuthenticatedUser authenticatedUser = AuthenticatedUser.newBuilder()
                    .withUser(user.get())
                    .addRole(Role.PLAYER)
                    .build();
            return AuthenticationSessionToken.newBuilder()
                    .withToken(jwtUtils.generateToken(authenticatedUser))
                    .withUser(authenticatedUser)
                    .build();
        } else {
            throw new IllegalArgumentException("Bad auth request");
        }
    }

    @Override
    public AuthenticationSessionToken refreshToken(final String token) {
        checkArgument(jwtUtils.isTokenValid(token), "Invalid token");
        final AuthenticatedUser authenticatedUser = jwtUtils.retrieveUser(token);
        return AuthenticationSessionToken.newBuilder()
                .withToken(jwtUtils.generateToken(authenticatedUser))
                .withUser(authenticatedUser)
                .build();
    }

    @Override
    public AuthenticationSessionToken registerAndAuthenticateUser(final RegistrationRequest registrationRequest) {
        final Optional<User> user = userAuthStore.registerUser(registrationRequest.getUserName(),
                registrationRequest.getPassword(),
                registrationRequest.getDisplayName());
        if (user.isPresent()) {
            return authenticateUser(AuthenticationRequest.newBuilder()
                    .withUserName(registrationRequest.getUserName())
                    .withPassword(registrationRequest.getPassword())
                    .build());
        }

        throw new IllegalArgumentException("Bad auth request");
    }
}
