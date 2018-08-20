package com.gmo.playing.cards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Deck of cards
 *
 * @author tedelen
 */
public class Deck {
    private final Deque<Card> cards;

    private Deck(final Collection<Card> cards) {
        final List<Card> unshuffled = new ArrayList<>(cards);
        Collections.shuffle(unshuffled);
        this.cards = new LinkedList<>(unshuffled);
    }

    /**
     * Creates a shuffled deck with one card for every given {@link Suit}, {@link CardRank} combination
     *
     * @param suits {@link Suit}s in the deck
     * @param ranks {@link CardRank}s in the deck
     *
     * @return shuffled deck with one card for every given {@link Suit}, {@link CardRank} combination
     */
    public static Deck shuffled(final Collection<Suit> suits, final Collection<CardRank> ranks) {
        final ImmutableList.Builder<Card> cards = ImmutableList.builder();
        for (final Suit suit : suits) {
            for (final CardRank rank : ranks) {
                cards.add(Card.newBuilder().withRank(rank).withSuit(suit).build());
            }
        }
        return new Deck(cards.build());
    }

    public synchronized List<Card> draw(final int numCards) {
        if (numCards > cards.size()) {
            throw new IllegalStateException(String.format("Cannot draw %d cards, only %d cards remain in deck.", numCards, cards.size()));
        }

        final ImmutableList.Builder<Card> cardsBuilder = ImmutableList.builder();
        for (int i = 0; i < numCards; i++) {
            cardsBuilder.add(cards.pop());
        }

        return cardsBuilder.build();
    }

    public synchronized int cardsRemaining() {
        return cards.size();
    }

    public synchronized boolean isEmpty() {
        return cards.isEmpty();
    }
}
