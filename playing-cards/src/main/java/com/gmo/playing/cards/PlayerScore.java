package com.gmo.playing.cards;

import java.util.Objects;
import java.util.StringJoiner;

public class PlayerScore {
    private final Player player;
    private final int score;

    public PlayerScore(final Player player, final int score) {
        this.player = player;
        this.score = score;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlayerScore that = (PlayerScore) o;
        return score == that.score &&
                Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, score);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlayerScore.class.getSimpleName() + "[", "]")
                .add("player=" + player)
                .add("score=" + score)
                .toString();
    }
}
