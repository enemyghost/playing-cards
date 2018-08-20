package com.gmo.playing.cards;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a playing card with a {@link CardRank} and {@link Suit}
 *
 * @author tedelen
 */
@JsonDeserialize(builder = Card.Builder.class)
public class Card implements Comparable<Card> {
    private final CardRank rank;
    private final Suit suit;

    private Card(Builder builder) {
        rank = builder.rank;
        suit = builder.suit;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public CardRank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(rank, card.rank) &&
                Objects.equals(suit, card.suit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    @Override
    public int compareTo(final Card other) {
        if (Objects.isNull(other)) {
            return 1;
        } else if (!other.rank.equals(this.rank)) {
            return this.rank.compareTo(other.rank);
        }

        return this.suit.compareTo(other.suit);
    }

    @Override
    public String toString() {
        return rank.getCardName().getSymbol() + suit.getSuitName().getSymbol();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private CardRank rank;
        private Suit suit;

        private Builder() {
        }

        public Builder withRank(final CardRank rank) {
            this.rank = rank;
            return this;
        }

        public Builder withSuit(final Suit suit) {
            this.suit = suit;
            return this;
        }

        public Card build() {
            requireNonNull(rank, "Null rank");
            requireNonNull(suit, "Null suit");
            return new Card(this);
        }
    }
}
