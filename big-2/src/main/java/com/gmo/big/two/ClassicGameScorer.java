package com.gmo.big.two;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.gmo.playing.cards.Hand;
import com.gmo.playing.cards.Player;
import com.google.common.base.MoreObjects;

/**
 * Scores the game with the following rules:
 * - If a player has cards remaining, their score is (-1 * numCards) * max(1, (2 * numTwos))
 * - If the player has 13 cards remaining, this number is multiplied by 3
 * - If the player has 9-12 cards remaining, this number is multiplied by 2
 * - The winner's score is the absolute value of the sum of all other player's scores
 * @author tedelen
 */
public class ClassicGameScorer implements GameScorer {
    public static final ClassicGameScorer DEFAULT = new ClassicGameScorer();

    private final HandScorer handScorer;

    public ClassicGameScorer() {
        this(ClassicHandScorer.INSTANCE);
    }

    public ClassicGameScorer(final HandScorer handScorer) {
        this.handScorer = Objects.requireNonNull(handScorer, "Null hand scorer");
    }

    @Override
    public Map<Player, Integer> score(final Big2Game game) {
        if (!game.isCompleted()) {
            return Collections.emptyMap();
        }

        final Map<Player, Hand> finalHands = game.getFinalHands();
        final Map<Player, Integer> scored = new HashMap<>();
        int winnerScore = 0;
        for (final Entry<Player, Hand> entry : finalHands.entrySet()) {
            if (entry.getValue().cardsRemaining() > 0) {
                final int loserScore = handScorer.score(entry.getValue());
                scored.put(entry.getKey(), loserScore);
                winnerScore = winnerScore + Math.abs(loserScore);
            }
        }
        scored.put(game.getWinner().orElseThrow(() -> new RuntimeException("ain't no winner")), winnerScore);
        return scored;
    }
}
