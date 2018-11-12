package com.gmo.big.two.auth.utils;

import java.util.Objects;

import com.google.common.base.Strings;

/**
 * Miscellaneous utilities
 *
 * @author csueiras
 */
public final class Utils {
    private Utils() {}

    public static void requireNonNullOrEmpty(final String val, final String message) {
        Objects.requireNonNull(Strings.emptyToNull(val), message);
    }

    public static void requireNonNullOrEmpty(final String val) {
        Objects.requireNonNull(Strings.emptyToNull(val));
    }
}
