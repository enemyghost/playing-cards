package com.gmo.playing.cards;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a card's suit. Suits may be ranked by passing a value into the constructor. For non-ranked suits, use the
 * same {@code value} for every suit.
 *
 * @author tedelen
 */
@JsonDeserialize(builder = Suit.Builder.class)
public class Suit implements Comparable<Suit> {
    public enum SuitName {
        DIAMONDS("d", "\u2662"),
        CLUBS("c", "\u2663"),
        HEARTS("h", "\u2661"),
        SPADES("s", "\u2660");

        private final String abbreviation;
        private final String symbol;

        SuitName(final String abbreviation, final String symbol) {
            this.abbreviation = abbreviation;
            this.symbol = symbol;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final SuitName suitName;
    private final int suitValue;

    private Suit(Builder builder) {
        suitName = builder.suitName;
        suitValue = builder.suitValue;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SuitName getSuitName() {
        return suitName;
    }

    public int getSuitValue() {
        return suitValue;
    }

    public String getSymbol() {
        return suitName.symbol;
    }

    public String getAbbreviation() {
        return suitName.abbreviation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suit suit = (Suit) o;
        return suitValue == suit.suitValue &&
                suitName == suit.suitName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suitName, suitValue);
    }

    @Override
    public int compareTo(final Suit other) {
        if (Objects.isNull(other)) {
            return 1;
        }
        return this.suitValue - other.suitValue;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private SuitName suitName;
        private int suitValue;

        private Builder() {
        }

        public Builder withSuitName(SuitName suitName) {
            this.suitName = suitName;
            return this;
        }

        public Builder withSuitValue(int suitValue) {
            this.suitValue = suitValue;
            return this;
        }

        public Suit build() {
            Objects.requireNonNull(suitName, "Null suit name");
            return new Suit(this);
        }
    }
}
