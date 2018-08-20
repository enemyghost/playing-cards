package com.gmo.big.two;

import java.util.Collection;

import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.Player;
import com.google.common.collect.ImmutableList;

/**
 * Player-caused exceptions that can occur in a big 2 game
 *
 * @author tedelen
 */
public class Big2Exception extends Exception {
    public Big2Exception(final String message) {
        super(message);
    }

    public static class GameOverException extends Big2Exception {
        private final Player errantPlayer;
        private final Player winningPlayer;
        public GameOverException(final Player errantPlayer, final Player winningPlayer) {
            super(String.format("%s cannot play because %s already won!", errantPlayer.getName(), winningPlayer.getName()));
            this.errantPlayer = errantPlayer;
            this.winningPlayer = winningPlayer;
        }

        public GameOverException(final Player errantPlayer) {
            super(String.format("%s cannot play, the game has ended", errantPlayer.getName()));
            this.errantPlayer = errantPlayer;
            this.winningPlayer = null;
        }
    }
    public static class NotYourTurnException extends Big2Exception {
        private final Player errantPlayer;
        private final Player currentPlayer;
        public NotYourTurnException(final Player currentPlayer, final Player errantPlayer) {
            super(String.format("It is %s's turn, %s cannot play.", currentPlayer.getName(), errantPlayer.getName()));
            this.errantPlayer = errantPlayer;
            this.currentPlayer = currentPlayer;
        }

        public Player getErrantPlayer() {
            return errantPlayer;
        }

        public Player getCurrentPlayer() {
            return currentPlayer;
        }
    }

    public static class PlayerDoesNotHaveCardsException extends Big2Exception {
        private final Player errantPlayer;
        public PlayerDoesNotHaveCardsException(final Player errantPlayer) {
            super(String.format("%s does not have the cards requested", errantPlayer.getName()));
            this.errantPlayer = errantPlayer;
        }

        public Player getErrantPlayer() {
            return errantPlayer;
        }
    }

    public static class TooManyCardsException extends Big2Exception {
        private final Player errantPlayer;
        public TooManyCardsException(final Player errantPlayer) {
            super("You cannot play more than five cards");
            this.errantPlayer = errantPlayer;
        }

        public Player getErrantPlayer() {
            return errantPlayer;
        }
    }

    public static class IllegalPlayException extends Big2Exception {
        private final Player errantPlayer;
        private final Collection<Card> previousPlay;

        public IllegalPlayException(final Player errantPlayer, final Collection<Card> previousPlay) {
            super(String.format("Illegal play, you must play a higher ranking hand with the same number of cards as the previous hand: %s", previousPlay));
            this.errantPlayer = errantPlayer;
            this.previousPlay = ImmutableList.copyOf(previousPlay);
        }
    }
}
