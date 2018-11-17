package com.gmo.big2.api.servlet;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import com.gmo.big2.auth.entities.AuthenticatedUser;

/**
 * Utilities for authentication / servlet
 *
 * @author csueiras
 */
public final class ServletAuthUtils {

    public static final String REQUEST_ATTR_USER = "user";

    private ServletAuthUtils() {}

    public static AuthenticatedUser retrieveUser(final HttpServletRequest request) {
        return (AuthenticatedUser) request.getAttribute(REQUEST_ATTR_USER);
    }

    public static void saveUserToRequest(final AuthenticatedUser authenticatedUser, final HttpServletRequest request) {
        request.setAttribute(REQUEST_ATTR_USER, authenticatedUser);
    }

    public static Optional<String> attemptExtractAuthorizationJwtToken(final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.ofNullable(authHeader.split("\\s")[1]);
    }
}
