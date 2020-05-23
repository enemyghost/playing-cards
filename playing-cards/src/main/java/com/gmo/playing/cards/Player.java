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
    private final boolean isBot;

    private Player(final Builder builder) {
        id = builder.id;
        name = builder.name;
        isBot = builder.isBot;
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

    public boolean isBot() {
        return isBot;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Player player = (Player) o;
        return isBot == player.isBot &&
                Objects.equals(id, player.id) &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isBot);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("isBot", isBot)
                .toString();
    }

    @JsonDeserialize
    public static final class Builder {
        private UUID id;
        private String name;
        private boolean isBot;

        private Builder() {
            isBot = false;
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder isBot() {
            this.isBot = true;
            return this;
        }

        public Builder withBot(final boolean isBot) {
            this.isBot = isBot;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
