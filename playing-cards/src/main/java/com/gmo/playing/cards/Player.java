package com.gmo.playing.cards;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
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
