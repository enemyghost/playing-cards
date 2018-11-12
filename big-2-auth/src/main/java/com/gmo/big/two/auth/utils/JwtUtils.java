package com.gmo.big.two.auth.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmo.big.two.auth.entities.AuthenticatedUser;
import com.gmo.big.two.auth.entities.Role;
import com.gmo.big.two.auth.entities.User;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Jwt utility functions
 */
public class JwtUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);

    public static final String ENV_CONSOLE_API_SECRET = "BIG_2_API_SECRET";
    public static final String JWT_AUDIENCE = "console-api";
    public static final String CLAIM_ROLES = "roles";

    private static final Splitter SPLITTER = Splitter.on(",");
    private static final Joiner JOINER = Joiner.on(",");

    private Clock clock;
    private Supplier<String> secretSupplier;
    private final Duration tokenDuration;

    public JwtUtils(final Duration tokenDuration) {
        this.tokenDuration = tokenDuration;
        clock = Clock.systemUTC();
        
        secretSupplier = () -> System.getenv(ENV_CONSOLE_API_SECRET);
    }

    /**
     * Determines if the given JWT is valid, the token might be invalid for several reasons such as: expired token,
     * crypto key, and others.
     * 
     * @param token Json Web Token
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(final String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getKey())
                    .requireAudience(JWT_AUDIENCE)
                    .parseClaimsJws(token);
            return true;
        } catch (final Exception e) {
            LOG.debug("Token parser threw an exception while attempting to validate", e);
            return false;
        }
    }

    /**
     * Creates a new Json Web Token for the given {@link AuthenticatedUser}
     * 
     * @param user
     *            {@link AuthenticatedUser}
     * @return Json Web Token
     */
    public String generateToken(final AuthenticatedUser user) {
        Objects.requireNonNull(user, "Null user");
        return Jwts.builder()
                .setClaims(user.getUser().payload())
                .setAudience(JWT_AUDIENCE)
                .signWith(SignatureAlgorithm.HS512, getKey())
                .claim(CLAIM_ROLES, JOINER.join(user.getRoles()))
                .setExpiration(Date.from(Instant.now(clock).plus(tokenDuration)))
                .compact();
    }

    /**
     * Extracts the {@link AuthenticatedUser} from the given Json Web Token
     * 
     * @param sessionToken
     *            Active json web token
     * @return {@link AuthenticatedUser}
     * @throws IllegalArgumentException
     *             if the given {@code sessionToken} is invalid
     */
    public AuthenticatedUser retrieveUser(final String sessionToken) {
        checkArgument(isTokenValid(sessionToken), "Invalid token supplied");

        final Claims claims = Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(sessionToken)
                .getBody();
        final Set<Role> roles = SPLITTER.splitToList((String)claims.get(CLAIM_ROLES))
                .stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return AuthenticatedUser.newBuilder()
                .withUser(User.fromPayload(claims))
                .withRoles(roles)
                .build();
    }

    private String getKey() {
        return MoreObjects.firstNonNull(Strings.emptyToNull(secretSupplier.get()), "DEFAULT-SECRET");
    }

    public void setClock(final Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Null clock");
    }

    public void setSecretSupplier(final Supplier<String> secretSupplier) {
        this.secretSupplier = Objects.requireNonNull(secretSupplier, "Null secret supplier");
    }
}
