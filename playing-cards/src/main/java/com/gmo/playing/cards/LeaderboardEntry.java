package com.gmo.playing.cards;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.playing.cards.LeaderboardEntry.Builder;
import com.google.common.base.MoreObjects;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Builder.class)
public class LeaderboardEntry {
    private final Player player;
    private final int score;
    private final int gamesWon;

    private LeaderboardEntry(final Builder builder) {
        player = builder.player;
        score = builder.score;
        gamesWon = builder.gamesWon;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardEntry that = (LeaderboardEntry) o;
        return score == that.score &&
                gamesWon == that.gamesWon &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, score, gamesWon);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("player", player)
                .add("score", score)
                .add("gamesWon", gamesWon)
                .toString();
    }

    public static final class Builder {
        private Player player;
        private int score;
        private int gamesWon;

        private Builder() {
        }

        public Builder withPlayer(final Player player) {
            this.player = player;
            return this;
        }

        public Builder withScore(final int score) {
            this.score = score;
            return this;
        }

        public Builder withGamesWon(final int gamesWon) {
            this.gamesWon = gamesWon;
            return this;
        }

        public LeaderboardEntry build() {
            return new LeaderboardEntry(this);
        }
    }
}
