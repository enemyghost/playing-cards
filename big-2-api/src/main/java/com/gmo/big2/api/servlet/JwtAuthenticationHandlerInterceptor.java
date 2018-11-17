package com.gmo.big2.api.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gmo.big2.auth.entities.AuthenticatedUser;
import com.gmo.big.two.auth.utils.JwtUtils;
import com.gmo.big2.api.annotation.AdminRequired;
import com.gmo.big2.api.annotation.LoginNotRequired;

/**
 * Intercepts requests, validates login (if necessary) and enforces basic role validation
 */
public class JwtAuthenticationHandlerInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    /**
     * Ctor.
     * 
     * @param jwtUtils
     *            {@link JwtUtils}
     */
    public JwtAuthenticationHandlerInterceptor(final JwtUtils jwtUtils) {
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
    }

    /**
     * Performs authentication and authorization checks
     * 
     * @param httpServletRequest
     *            {@link HttpServletRequest}
     * @param httpServletResponse
     *            {@link HttpServletResponse}
     * @param handler
     *            {@link HandlerMethod}
     * @return {@code true} if the execution chain should proceed with the next interceptor or the handler itself. Else,
     *         DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     * @throws Exception
     *             in case of errors
     */
    @Override
    public boolean preHandle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Object handler) throws Exception {
        if (httpServletRequest.getMethod().equals("OPTIONS")) {
            return true;
        }
        if (!(handler instanceof  HandlerMethod)) {
            return true;
        }

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final LoginNotRequired loginNotRequired = handlerMethod.getMethod().getAnnotation(LoginNotRequired.class);
        final AdminRequired adminRequired = handlerMethod.getMethod().getAnnotation(AdminRequired.class);
        // By default we always require login
        final boolean isLoginRequired = loginNotRequired == null;
        final boolean isAdminRequired = adminRequired != null;

        if (!isLoginRequired) {
            // Early exit, there's no need to check credentials if this endpoint requires no auth
            return true;
        }

        // Validate token
        final Optional<String> tokenOpt = ServletAuthUtils.attemptExtractAuthorizationJwtToken(httpServletRequest);
        if (!tokenOpt.isPresent()) {
            // No token in the request, user is not authenticated 401 Unauthorized
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        final String token = tokenOpt.get();

        // If token is valid, proceed and pass the {@link AuthenticatedUser} entity to the request
        if (jwtUtils.isTokenValid(token)) {
            final AuthenticatedUser user = jwtUtils.retrieveUser(token);
            ServletAuthUtils.saveUserToRequest(jwtUtils.retrieveUser(token), httpServletRequest);

            // First lets evaluate if we require only admin users
            if (isAdminRequired) {
                // If the user is an admin nothing else to validate
                if (user.isAdmin()) {
                    return true;
                } else {
                    // User is not an admin so 403 Forbidden
                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return false;
                }
            }

            return true;
        }

        // We are here if we have an invalid token, 401 Unauthorized
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;

    }

    @Override
    public void postHandle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Object o, final ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Object o, final Exception e) throws Exception {

    }
}
