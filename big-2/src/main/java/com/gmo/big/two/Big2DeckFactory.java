package com.gmo.big.two;

import java.util.Collection;

import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.CardRank;
import com.gmo.playing.cards.CardRank.CardName;
import com.gmo.playing.cards.Deck;
import com.gmo.playing.cards.Suit;
import com.gmo.playing.cards.Suit.SuitName;
import com.google.common.collect.ImmutableList;

/**
 * Creates a Big 2 deck, sorted D, C, H, S with two being the highest valued card.
 *
 * @author tedelen
 */
public class Big2DeckFactory {
    private static final Collection<Suit> SUITS = ImmutableList.of(
            Suit.newBuilder().withSuitName(SuitName.DIAMONDS).withSuitValue(0).build(),
            Suit.newBuilder().withSuitName(SuitName.CLUBS).withSuitValue(1).build(),
            Suit.newBuilder().withSuitName(SuitName.HEARTS).withSuitValue(2).build(),
            Suit.newBuilder().withSuitName(SuitName.SPADES).withSuitValue(3).build()
    );
    private static final Collection<CardRank> RANKS = ImmutableList.of(
            CardRank.newBuilder().withCardName(CardName.THREE).withRank(3).build(),
            CardRank.newBuilder().withCardName(CardName.FOUR).withRank(4).build(),
            CardRank.newBuilder().withCardName(CardName.FIVE).withRank(5).build(),
            CardRank.newBuilder().withCardName(CardName.SIX).withRank(6).build(),
            CardRank.newBuilder().withCardName(CardName.SEVEN).withRank(7).build(),
            CardRank.newBuilder().withCardName(CardName.EIGHT).withRank(8).build(),
            CardRank.newBuilder().withCardName(CardName.NINE).withRank(9).build(),
            CardRank.newBuilder().withCardName(CardName.TEN).withRank(10).build(),
            CardRank.newBuilder().withCardName(CardName.JACK).withRank(11).build(),
            CardRank.newBuilder().withCardName(CardName.QUEEN).withRank(12).build(),
            CardRank.newBuilder().withCardName(CardName.KING).withRank(13).build(),
            CardRank.newBuilder().withCardName(CardName.ACE).withRank(14).build(),
            CardRank.newBuilder().withCardName(CardName.TWO).withRank(15).build()
    );

    public static Deck getShuffledBig2Deck() {
        return Deck.shuffled(SUITS, RANKS);
    }

    public static Card getByAbbrev(final String rank, final String suit) {
        return Card.newBuilder()
                .withRank(RANKS.stream().filter(r -> r.getCardName().getSymbol().equalsIgnoreCase(rank)).findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find suit")))
                .withSuit(SUITS.stream().filter(s -> s.getSuitName().getAbbreviation().equalsIgnoreCase(suit)).findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find suit")))
                .build();
    }
}
