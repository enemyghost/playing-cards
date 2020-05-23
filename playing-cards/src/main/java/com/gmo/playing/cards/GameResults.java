package com.gmo.playing.cards;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class GameResults {
    private final List<GameResult> results;

    public GameResults(final List<GameResult> results) {
        this.results = List.copyOf(Objects.requireNonNull(results, "Null results"));
    }

    public List<GameResult> getResults() {
        return results;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GameResults that = (GameResults) o;
        return Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GameResults.class.getSimpleName() + "[", "]")
                .add("results=" + results)
                .toString();
    }
}
