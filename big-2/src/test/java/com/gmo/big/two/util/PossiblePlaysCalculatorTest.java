package com.gmo.big.two.util;

import com.gmo.big.two.Big2DeckFactory;
import com.gmo.playing.cards.Card;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PossiblePlaysCalculatorTest {
    @Test
    public void testPossiblePlaysEmptyHand() {
        assertEquals(Collections.emptyMap(), PossiblePlaysCalculator.getPossiblePlaysByCardCount(Collections.emptySet()));
    }

    @Test
    public void testPossiblePlaysOneCard() {
        final Card c = Big2DeckFactory.getByAbbrev("3", "c");
        final List<Card> hand = ImmutableList.of(c);
        assertEquals(ImmutableMap.of(1, ImmutableList.of(Collections.singletonList(c))),
                PossiblePlaysCalculator.getPossiblePlaysByCardCount(hand));
    }

    @Test
    public void testPossiblePlaysAllKindsOfShit() {
        final List<Card> hand = ImmutableList.of(
                Big2DeckFactory.getByAbbrev("Q", "s"),
                Big2DeckFactory.getByAbbrev("Q", "h"),
                Big2DeckFactory.getByAbbrev("J", "h"),
                Big2DeckFactory.getByAbbrev("10", "h"),
                Big2DeckFactory.getByAbbrev("9", "h"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("8", "c"),
                Big2DeckFactory.getByAbbrev("7", "s"),
                Big2DeckFactory.getByAbbrev("5", "h"),
                Big2DeckFactory.getByAbbrev("3", "d"),
                Big2DeckFactory.getByAbbrev("3", "s"),
                Big2DeckFactory.getByAbbrev("3", "h"),
                Big2DeckFactory.getByAbbrev("3", "c")
        );
        final Map<Integer, List<List<Card>>> possiblePlaysByCardCount = PossiblePlaysCalculator.getPossiblePlaysByCardCount(hand);
        // 1, 2, 3, and 5-card plays exist
        assertEquals(4, possiblePlaysByCardCount.size());
        // Every card is a 1 card play
        assertEquals(13, possiblePlaysByCardCount.get(1).size());
        // 6 combinations of 33, plus 88 and QQ
        assertEquals(8, possiblePlaysByCardCount.get(2).size());
        // 4 combinations of trip 3s
        assertEquals(4, possiblePlaysByCardCount.get(3).size());
        //    6 straights
        // + 21 flushes (7 choose 5)
        // +  8 full houses (4 choose 3 * 2)
        // +  9 quads
        // -  1 straight that is also a flush
        assertEquals(43, possiblePlaysByCardCount.get(5).size());
    }

    @Test
    public void testPossiblePlaysAllKindsOfFlush() {
        final List<Card> hand = ImmutableList.of(
                Big2DeckFactory.getByAbbrev("2", "h"),
                Big2DeckFactory.getByAbbrev("A", "h"),
                Big2DeckFactory.getByAbbrev("K", "h"),
                Big2DeckFactory.getByAbbrev("Q", "h"),
                Big2DeckFactory.getByAbbrev("J", "h"),
                Big2DeckFactory.getByAbbrev("10", "h"),
                Big2DeckFactory.getByAbbrev("9", "h"),
                Big2DeckFactory.getByAbbrev("8", "h"),
                Big2DeckFactory.getByAbbrev("7", "h"),
                Big2DeckFactory.getByAbbrev("6", "h"),
                Big2DeckFactory.getByAbbrev("5", "h"),
                Big2DeckFactory.getByAbbrev("4", "h"),
                Big2DeckFactory.getByAbbrev("3", "h")
        );
        final Map<Integer, List<List<Card>>> possiblePlaysByCardCount = PossiblePlaysCalculator.getPossiblePlaysByCardCount(hand);
        assertEquals(1300, possiblePlaysByCardCount.values().stream().mapToInt(List::size).sum());
    }
}
