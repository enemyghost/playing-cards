package com.gmo.big.two;

import com.gmo.playing.cards.Hand;

/**
 * Scores a non-empty hand
 *
 * @author tedelen
 */
public interface HandScorer {
    /**
     * Scores a non-empty hand
     *
     * @param hand {@link Hand} to score
     *
     * @return the hand's score
     */
    int score(final Hand hand);
}
