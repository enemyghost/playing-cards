package com.gmo.playing.cards;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Rank of a playing card.
 *
 * @author tedelen
 */
@JsonDeserialize(builder = CardRank.Builder.class)
public class CardRank implements Comparable<CardRank> {
    public enum CardName {
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");

        private final String symbol;

        CardName(final String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final CardName cardName;
    private final int rank;

    private CardRank(Builder builder) {
        cardName = builder.cardName;
        rank = builder.rank;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public CardName getCardName() {
        return cardName;
    }

    public int getRank() {
        return rank;
    }

    public String getSymbol() { return cardName.symbol; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRank cardRank = (CardRank) o;
        return rank == cardRank.rank &&
                cardName == cardRank.cardName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, rank);
    }

    @Override
    public int compareTo(final CardRank other) {
        if (Objects.isNull(other)) {
            return 1;
        }
        return this.rank - other.rank;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private CardName cardName;
        private int rank;

        private Builder() {
        }

        public Builder withCardName(CardName cardName) {
            this.cardName = cardName;
            return this;
        }

        public Builder withRank(int rank) {
            this.rank = rank;
            return this;
        }

        public CardRank build() {
            Objects.requireNonNull(cardName, "Null card name");
            Objects.requireNonNull(rank, "Null rank");
            return new CardRank(this);
        }
    }
}
