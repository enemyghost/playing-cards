package com.gmo.playing.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.playing.cards.GameResult.Builder;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Contains the final scores of a game
 */
@JsonDeserialize(builder = Builder.class)
public class GameResult {
    private final UUID gameId;
    private final Instant gameCompletedInstant;
    private final Map<Player, Integer> playerScores;

    private GameResult(final Builder builder) {
        gameId = builder.gameId;
        gameCompletedInstant = builder.gameCompletedInstant;
        playerScores = Map.copyOf(builder.playerScores);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UUID getGameId() {
        return gameId;
    }

    public Instant getGameCompletedInstant() {
        return gameCompletedInstant;
    }

    public Map<Player, Integer> getPlayerScores() {
        return playerScores;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GameResult that = (GameResult) o;
        return Objects.equals(gameId, that.gameId) &&
                Objects.equals(gameCompletedInstant, that.gameCompletedInstant) &&
                Objects.equals(playerScores, that.playerScores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, gameCompletedInstant, playerScores);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GameResult.class.getSimpleName() + "[", "]")
                .add("gameId=" + gameId)
                .add("gameCompletedInstant=" + gameCompletedInstant)
                .add("playerScores=" + playerScores)
                .toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private UUID gameId;
        private Instant gameCompletedInstant;
        private Map<Player, Integer> playerScores;

        private Builder() {
            playerScores = new HashMap<>();
        }

        public Builder withGameId(final UUID val) {
            gameId = val;
            return this;
        }

        public Builder withGameCompletedInstant(final Instant val) {
            gameCompletedInstant = val;
            return this;
        }

        public Builder addPlayerScore(final Player player, final int score) {
            playerScores.put(player, score);
            return this;
        }

        public Builder withPlayerScores(final Map<Player, Integer> val) {
            playerScores = new HashMap<>(Objects.requireNonNull(val, "Null player scores map"));
            return this;
        }

        public GameResult build() {
            Objects.requireNonNull(gameId, "Null game ID");
            Objects.requireNonNull(gameCompletedInstant, "Null completed instant");
            Preconditions.checkArgument(playerScores.size() >= 2);
            return new GameResult(this);
        }
    }
}
