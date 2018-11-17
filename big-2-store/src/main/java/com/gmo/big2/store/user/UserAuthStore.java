package com.gmo.big2.store.user;

import java.util.Optional;

import com.gmo.big2.auth.entities.User;

/**
 * @author tedelen
 */
public interface UserAuthStore {
    Optional<User> authenticate(final String userName, final String password);

    Optional<User> registerUser(final String userName, final String password, final String displayName);
}
