package com.gmo.big.two;

import java.util.Map;

import com.gmo.playing.cards.Player;

/**
 * For a finished {@link Big2Game}, assigns a score to each player.
 *
 * @author tedelen
 */
@FunctionalInterface
public interface GameScorer {
    /**
     * For a finished {@link Big2Game}, assigns a score to each player.
     *
     * @param game completed {@link Big2Game} to score
     *
     * @return mapping of {@link Player} in the game to their final score
     */
    Map<Player, Integer> score(final Big2Game game);
}
