package com.gmo.playing.cards;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gmo.playing.cards.CardRank.CardName;
import com.gmo.playing.cards.Suit.SuitName;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class DeckTest {
    private Deck shuffled;

    @Before
    public void setup() {
        shuffled = Deck.shuffled(
                ImmutableList.of(
                        Suit.newBuilder().withSuitName(SuitName.DIAMONDS).withSuitValue(0).build(),
                        Suit.newBuilder().withSuitName(SuitName.CLUBS).withSuitValue(1).build()),
                ImmutableList.of(
                        CardRank.newBuilder().withCardName(CardName.TWO).withRank(2).build(),
                        CardRank.newBuilder().withCardName(CardName.THREE).withRank(3).build(),
                        CardRank.newBuilder().withCardName(CardName.FOUR).withRank(4).build()
                ));
    }

    @Test
    public void testShuffled() {
        assertEquals(6, shuffled.cardsRemaining());
    }

    @Test
    public void testDraw() {
        final Card card = Iterables.getOnlyElement(shuffled.draw(1));
        assertEquals(5, shuffled.cardsRemaining());
        final List<Card> cards = shuffled.draw(5);
        assertEquals(5, cards.size());
        assertEquals(0, shuffled.cardsRemaining());
    }

    @Test(expected = IllegalStateException.class)
    public void testDrawTooMany() {
        shuffled.draw(7);
    }
}
