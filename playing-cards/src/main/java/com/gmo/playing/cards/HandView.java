package com.gmo.playing.cards;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * A view of a hand that may or may not contain the actual cards
 *
 * @author tedelen
 */
public class HandView {
    private final Player player;
    private final int cardCount;
    private final List<Card> cards;
    private final int position;
    private final boolean isDealer;
    private final boolean isNextToPlay;

    private HandView(final Builder builder) {
        player = builder.player;
        cardCount = builder.cardCount;
        cards = builder.cards;
        position = builder.position;
        isDealer = builder.isDealer;
        isNextToPlay = builder.isNextToPlay;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Player getPlayer() {
        return player;
    }

    public int getCardCount() {
        return cardCount;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getPosition() {
        return position;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public boolean isNextToPlay() {
        return isNextToPlay;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandView handView = (HandView) o;
        return cardCount == handView.cardCount &&
                position == handView.position &&
                isDealer == handView.isDealer &&
                isNextToPlay == handView.isNextToPlay &&
                Objects.equals(player, handView.player) &&
                Objects.equals(cards, handView.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, cardCount, cards, position, isDealer, isNextToPlay);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("player", player)
                .add("cardCount", cardCount)
                .add("cards", cards)
                .add("position", position)
                .add("isDealer", isDealer)
                .add("isNextToPlay", isNextToPlay)
                .toString();
    }

    public static final class Builder {
        private Player player;
        private int cardCount;
        private List<Card> cards;
        private int position;
        private boolean isDealer;
        private boolean isNextToPlay;

        private Builder() {
            cardCount = -1;
            position = -1;
            cards = Collections.emptyList();
        }

        public Builder withPlayer(final Player player) {
            this.player = player;
            return this;
        }

        public Builder withCardCount(final int cardCount) {
            this.cardCount = cardCount;
            return this;
        }

        public Builder withCards(final List<Card> cards) {
            this.cards = ImmutableList.copyOf(Objects.requireNonNull(cards));
            return this;
        }

        public Builder withPosition(final int position) {
            this.position = position;
            return this;
        }

        public Builder withIsDealer(final boolean isDealer) {
            this.isDealer = isDealer;
            return this;
        }

        public Builder withIsNextToPlay(final boolean isNextToPlay) {
            this.isNextToPlay = isNextToPlay;
            return this;
        }

        public HandView build() {
            Preconditions.checkArgument(!cards.isEmpty() || cardCount >= 0, "Must set card count or cards");
            if (!cards.isEmpty()) {
                cardCount = cards.size();
            }
            Preconditions.checkArgument(position >= 0 && position <= 3, "Position must be between 0 and 3");
            return new HandView(this);
        }
    }
}
