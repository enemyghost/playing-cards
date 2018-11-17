package com.gmo.big.two.auth.api;

import com.gmo.big2.auth.entities.AuthenticationRequest;
import com.gmo.big2.auth.entities.AuthenticationSessionToken;
import com.gmo.big2.auth.entities.RegistrationRequest;
import io.jsonwebtoken.Jwts;

/**
 * {@link Jwts} Authentication Provider
 *
 * @author malvarino
 */
public interface JsonWebTokenAuthenticationProvider {

    /**
     * Returns session token for authenticated users
     *
     * @param authenticationRequest - {@link AuthenticationRequest} Request for authenticaton
     * @return {@link AuthenticationSessionToken}
     * @throws IllegalArgumentException if {@link AuthenticationRequest} is invalid
     */
    AuthenticationSessionToken authenticateUser(final AuthenticationRequest authenticationRequest);

    /**
     * Refreshes a valid and <strong>unexpired</strong> session token (JWT), the new token will have a new expiration
     * date
     * 
     * @param token
     *            Json Web Token
     * @return {@link AuthenticationSessionToken}
     */
    AuthenticationSessionToken refreshToken(final String token);

    AuthenticationSessionToken registerAndAuthenticateUser(final RegistrationRequest authenticationRequest);
}
