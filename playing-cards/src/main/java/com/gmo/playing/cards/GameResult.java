package com.gmo.playing.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.playing.cards.GameResult.Builder;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private final List<PlayerScore> playerScores;

    private GameResult(final Builder builder) {
        gameId = builder.gameId;
        gameCompletedInstant = builder.gameCompletedInstant;
        playerScores = List.copyOf(builder.playerScores);
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

    public List<PlayerScore> getPlayerScores() {
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
        private List<PlayerScore> playerScores;

        private Builder() {
            playerScores = new ArrayList<>();
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
            playerScores.add(new PlayerScore(player, score));
            return this;
        }

        public GameResult build() {
            Objects.requireNonNull(gameId, "Null game ID");
            Objects.requireNonNull(gameCompletedInstant, "Null completed instant");
            Preconditions.checkArgument(playerScores.size() >= 2 && playerScores.size() <= 4);
            return new GameResult(this);
        }
    }
}
