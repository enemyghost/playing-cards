package com.gmo.big.two.bot;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gmo.big.two.Big2GameView;
import com.gmo.big.two.Big2Play;
import com.gmo.playing.cards.HandView;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
public interface Big2Bot {
    /**
     * Provided the current {@link Big2GameView}, returns the next {@link Big2Play} the bot would like to make
     */
    default Big2Play play(final Big2GameView gameView) {
        return gameView.playToBeat()
                .map(h -> play(gameView.nextPlayerHand(), h))
                .orElse(play(gameView.nextPlayerHand()));
    }

    /**
     * Provided the current bot's {@link HandView}, returns the next {@link Big2Play} the bot would like to make. The
     * returned hand must be better than the given {@link Big2Play}
     *
     * If an illegal hand is returned, the play will be ignored and the bot will pass.
     */
    Big2Play play(final HandView myHand, final Big2Play playToBeat);

    /**
     * Provided the current bot's {@link HandView}, return the next {@link Big2Play} the bot would like to make.
     */
    Big2Play play(final HandView myHand);
}
