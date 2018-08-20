package com.gmo.playing.cards;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Strings;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Player.Builder.class)
public class Player {
    private final UUID id;
    private final String name;

    private Player(final Builder builder) {
        id = builder.id;
        name = builder.name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonDeserialize
    public static final class Builder {
        private UUID id;
        private String name;

        private Builder() {
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
