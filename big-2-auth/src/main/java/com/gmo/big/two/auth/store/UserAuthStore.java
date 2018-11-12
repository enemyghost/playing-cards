package com.gmo.big.two.auth.store;

import java.util.Optional;

import com.gmo.big.two.auth.entities.User;

/**
 * @author tedelen
 */
public interface UserAuthStore {
    Optional<User> authenticate(final String userName, final String password);

    Optional<User> registerUser(final String userName, final String password, final String displayName);
}
