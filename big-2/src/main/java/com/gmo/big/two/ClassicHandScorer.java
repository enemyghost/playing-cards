package com.gmo.big.two;

import com.gmo.playing.cards.CardRank.CardName;
import com.gmo.playing.cards.Hand;

/**
 * @author tedelen
 */
public class ClassicHandScorer implements HandScorer {
    public static final ClassicHandScorer INSTANCE = new ClassicHandScorer();

    private ClassicHandScorer() { }

    @Override
    public int score(final Hand hand) {
        int baseScore = hand.cardsRemaining();

        final int numTwos = (int)hand.getCards()
                .stream()
                .filter(t-> t.getRank().getCardName().equals(CardName.TWO))
                .count();
        baseScore = baseScore * (int)Math.pow(2, numTwos);
        if (hand.cardsRemaining() == 13) {
            baseScore = baseScore * 3;
        } else if (hand.cardsRemaining() >= 9) {
            baseScore = baseScore * 2;
        }

        return baseScore * -1;
    }
}
