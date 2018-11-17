package com.gmo.big.two;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.gmo.playing.cards.Hand;
import com.gmo.playing.cards.Player;
import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ClassicGameScorerTest {
    @Mock
    private HandScorer mockScorer;
    @Mock
    private Hand mockHandWinner;
    @Mock
    private Hand mockHand1;
    @Mock
    private Hand mockHand2;
    @Mock
    private Hand mockHand3;
    @Mock
    private Big2Game mockGame;
    @Mock
    private Player mockWinner;
    @Mock
    private Player player1;
    @Mock
    private Player player2;
    @Mock
    private Player player3;

    private ClassicGameScorer gameScorer;

    @Before
    public void setUp() throws Exception {
        gameScorer = new ClassicGameScorer(mockScorer);
    }

    @Test
    public void testGameScorer() {
        doReturn(0).when(mockHandWinner).cardsRemaining();
        doReturn(1).when(mockHand1).cardsRemaining();
        doReturn(2).when(mockHand2).cardsRemaining();
        doReturn(10).when(mockHand3).cardsRemaining();
        doReturn(ImmutableMap.of(
                player1, mockHand1,
                mockWinner, mockHandWinner,
                player2, mockHand2,
                player3, mockHand3
        )).when(mockGame).getFinalHands();
        doReturn(true).when(mockGame).isCompleted();
        doReturn(Optional.of(mockWinner)).when(mockGame).getWinner();
        doReturn(-1).when(mockScorer).score(mockHand1);
        doReturn(-2).when(mockScorer).score(mockHand2);
        doReturn(-20).when(mockScorer).score(mockHand3);
        assertEquals(ImmutableMap.of(
                player1, -1,
                player2, -2,
                player3, -20,
                mockWinner, 23
        ), gameScorer.score(mockGame));
    }

    @Test
    public void testGameScorerUncompletedGame() {
        doReturn(false).when(mockGame).isCompleted();
        assertEquals(Collections.emptyMap(), gameScorer.score(mockGame));
    }
}
