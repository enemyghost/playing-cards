package com.gmo.playing.cards;

import java.util.Collection;
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

    public HandView(final Player player, final int cardCount) {
        Preconditions.checkArgument(cardCount >= 0, "Card count cannot be negative");
        this.player = Objects.requireNonNull(player);
        this.cards = Collections.emptyList();
        this.cardCount = cardCount;
    }

    public HandView(final Player player, final Collection<Card> cards) {
        this.player = Objects.requireNonNull(player);
        this.cards = ImmutableList.copyOf(Objects.requireNonNull(cards));
        this.cardCount = cards.size();
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandView handView = (HandView) o;
        return cardCount == handView.cardCount &&
                Objects.equals(player, handView.player) &&
                Objects.equals(cards, handView.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, cardCount, cards);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("player", player)
                .add("cardCount", cardCount)
                .add("cards", cards)
                .toString();
    }
}
