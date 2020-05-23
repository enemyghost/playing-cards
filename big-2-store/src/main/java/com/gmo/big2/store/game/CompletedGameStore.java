package com.gmo.big2.store.game;

import java.util.List;

import com.gmo.big.two.Big2Game;
import com.gmo.playing.cards.GameResult;
import com.gmo.playing.cards.GameResults;
import com.gmo.playing.cards.LeaderboardEntry;

/**
 * @author tedelen
 */
public interface CompletedGameStore {
    void saveFinalGameState(final Big2Game completedBig2Game);
    List<LeaderboardEntry> leaderboard();
    GameResults history();
}
