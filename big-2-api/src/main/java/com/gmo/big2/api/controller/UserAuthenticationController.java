package com.gmo.big2.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gmo.big.two.auth.api.JsonWebTokenAuthenticationProvider;
import com.gmo.big.two.auth.entities.AuthenticatedUser;
import com.gmo.big.two.auth.entities.AuthenticationRequest;
import com.gmo.big.two.auth.entities.AuthenticationSessionToken;
import com.gmo.big.two.auth.entities.RegistrationRequest;
import com.gmo.big2.api.servlet.ServletAuthUtils;

/**
 * Authentication API
 */
@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = { "https://whispering-ocean-60773.herokuapp.com", "http://localhost:3000" }, allowCredentials = "true")
public class UserAuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationController.class);

    @Resource
    private JsonWebTokenAuthenticationProvider authProvider;

    /**
     * Authenticates a user, once authenticated a session JWT token is generated and returned in the response.
     * 
     * @param authenticationRequest
     *            {@link AuthenticationRequest}
     * @return {@link AuthenticationSessionToken}
     * @throws Exception
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public AuthenticationSessionToken authenticateUser(@RequestBody final AuthenticationRequest authenticationRequest) throws Exception {
        LOG.debug("Receive login request = {}", authenticationRequest);
        return authProvider.authenticateUser(authenticationRequest);
    }

    /**
     * Uses the {@code Authorization} header's token and returns a new token with a new expiration date.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return {@link AuthenticationSessionToken}
     * @throws Exception
     */
    @GetMapping(value = "/refreshToken")
    @ResponseBody
    public AuthenticationSessionToken refreshToken(final HttpServletRequest request) throws Exception {
        // Extract token from request, we know the user has a valid token otherwise wouldn't be able to reach this code
        final String token = ServletAuthUtils.attemptExtractAuthorizationJwtToken(request).get();

        // Return a fresh new token
        return authProvider.refreshToken(token);
    }

    /**
     * Returns the {@link AuthenticatedUser} in the current session
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @return {@link AuthenticatedUser}
     */
    @GetMapping(value = "/me")
    @ResponseBody
    public AuthenticatedUser retrieveUser(final HttpServletRequest request) {
        return ServletAuthUtils.retrieveUser(request);
    }

    @PostMapping(value = "/register")
    @ResponseBody
    public AuthenticationSessionToken registerAndAuthenticateUser(@RequestBody final RegistrationRequest registrationRequest) {
        LOG.debug("Receive registration request = {}", registrationRequest);
        if (registrationRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        return authProvider.registerAndAuthenticateUser(registrationRequest);
    }

    void setAuthProvider(final JsonWebTokenAuthenticationProvider authProvider) {
        this.authProvider = Objects.requireNonNull(authProvider, "Null auth provider");
    }
}
