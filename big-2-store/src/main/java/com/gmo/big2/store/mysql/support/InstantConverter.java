package com.gmo.big2.store.mysql.support;

import java.time.Instant;

import org.jooq.Converter;
import org.jooq.types.ULong;

/**
 * {@link Converter} for {@link ULong} to {@link Instant}, useful for converting bigint to a java Instant
 *
 * @author tedelen
 */
public class InstantConverter implements Converter<ULong, Instant> {

    public static final InstantConverter INSTANCE = new InstantConverter();

    private static final long serialVersionUID = 1L;

    @Override
    public Instant from(final ULong databaseObject) {
        if (databaseObject == null) {
            return null;
        }

        return Instant.ofEpochMilli(databaseObject.longValue());
    }

    @Override
    public ULong to(final Instant userObject) {
        if (userObject == null) {
            return null;
        }
        return ULong.valueOf(userObject.toEpochMilli());
    }

    @Override
    public Class<ULong> fromType() {
        return ULong.class;
    }

    @Override
    public Class<Instant> toType() {
        return Instant.class;
    }
}
