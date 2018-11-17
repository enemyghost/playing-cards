package com.gmo.big2.store.game;

import com.gmo.big.two.Big2Game;

/**
 * @author tedelen
 */
public interface CompletedGameStore {
    void saveFinalGameState(final Big2Game completedBig2Game);
}
