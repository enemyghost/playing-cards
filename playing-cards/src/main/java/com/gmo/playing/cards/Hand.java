package com.gmo.playing.cards;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A player's hand of cards in a game
 *
 * @author tedelen
 */
public class Hand {
    private final Deque<Card> cards;
    private final int capacity;
    private final Stack<Collection<Card>> history;

    public Hand(final Collection<Card> initialCards, final int capacity) {
        Preconditions.checkArgument(capacity > 0);
        cards = new LinkedList<>(Objects.requireNonNull(initialCards, "Null initial cards, pass empty collection to initialize empty hand"));
        this.capacity = capacity;
        history = new Stack<>();
        updateHistory();
    }

    public Hand(final int capacity) {
        this(Collections.emptyList(), capacity);
    }

    public Hand() {
        this(Integer.MAX_VALUE);
    }

    public List<Card> getCards() {
        return cards.stream()
                .sorted()
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<Collection<Card>> getHistory() {
        return ImmutableList.copyOf(history);
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized boolean canDraw() {
        return cards.size() < capacity;
    }

    public synchronized boolean addCard(final Card c) {
        if (!canDraw()) {
            return false;
        }

        final boolean result = cards.add(c);
        updateHistory();
        return result;
    }

    public synchronized boolean addCards(final Collection<Card> c) {
        if (cards.size() + c.size() <= capacity) {
            cards.addAll(c);
            updateHistory();
            return true;
        }
        return false;
    }

    public synchronized boolean containsCards(final Collection<Card> e) {
        return cards.containsAll(e);
    }

    public synchronized boolean removeCards(final Collection<Card> e) {
        if (!cards.containsAll(e)) {
            return false;
        }
        final boolean result = cards.removeAll(e);
        updateHistory();
        return result;
    }

    public synchronized int cardsRemaining() {
        return cards.size();
    }

    private void updateHistory() {
        history.push(ImmutableSet.copyOf(cards));
    }
}
