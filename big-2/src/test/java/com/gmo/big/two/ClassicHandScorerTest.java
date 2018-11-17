package com.gmo.big.two;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gmo.playing.cards.Hand;
import com.google.common.collect.ImmutableSet;

public class ClassicHandScorerTest {
    @Test
    public void testWorstHand() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("2", "s"),
                Big2DeckFactory.getByAbbrev("2", "h"),
                Big2DeckFactory.getByAbbrev("2", "c"),
                Big2DeckFactory.getByAbbrev("2", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s"),
                Big2DeckFactory.getByAbbrev("8", "c"),
                Big2DeckFactory.getByAbbrev("9", "s"),
                Big2DeckFactory.getByAbbrev("10", "h"),
                Big2DeckFactory.getByAbbrev("Q", "h"),
                Big2DeckFactory.getByAbbrev("K", "h")
        ), 13);

        assertEquals(-1 * 13 * 2 * 2 * 2 * 2 * 3, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testThirteenCards() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("K", "s"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("5", "c"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s"),
                Big2DeckFactory.getByAbbrev("8", "c"),
                Big2DeckFactory.getByAbbrev("9", "s"),
                Big2DeckFactory.getByAbbrev("10", "h"),
                Big2DeckFactory.getByAbbrev("Q", "h"),
                Big2DeckFactory.getByAbbrev("K", "h")
        ), 13);

        assertEquals(-1 * 13 * 3, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testOneCard() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("K", "s")), 13);
        assertEquals(-1, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testOneTwo() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("2", "d")), 13);
        assertEquals(-2, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testNineCardsWithTwo() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("2", "s"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("5", "c"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s"),
                Big2DeckFactory.getByAbbrev("8", "c")
        ), 13);

        assertEquals(-1 * 9 * 2 * 2, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testEightCardsWithTwo() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("2", "s"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("5", "c"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s")
        ), 13);

        assertEquals(-1 * 8 * 2, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testNineCards() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("K", "s"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("5", "c"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s"),
                Big2DeckFactory.getByAbbrev("8", "c")
        ), 13);

        assertEquals(-1 * 9 * 2, ClassicHandScorer.INSTANCE.score(hand));
    }

    @Test
    public void testEightCards() {
        final Hand hand = new Hand(ImmutableSet.of(
                Big2DeckFactory.getByAbbrev("K", "s"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("5", "c"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("4", "c"),
                Big2DeckFactory.getByAbbrev("5", "d"),
                Big2DeckFactory.getByAbbrev("7", "s")
        ), 13);

        assertEquals(-1 * 8, ClassicHandScorer.INSTANCE.score(hand));
    }
}
