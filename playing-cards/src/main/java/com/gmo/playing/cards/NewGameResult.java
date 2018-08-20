package com.gmo.playing.cards;

/**
 * @author tedelen
 */
public class NewGameResult<T> {
    public enum GameCreationStatus {
        /**
         * New game successfully created
         */
        CREATED,
        /**
         * Game already in progress
         */
        IN_PROGRESS,
        /**
         * Unable to create a new game
         */
        FAILED
    }

    private final T inProgressGame;
    private final GameCreationStatus status;
    private final String reason;

    private NewGameResult(final T inProgressGame, final GameCreationStatus status, final String reason) {
        this.inProgressGame = inProgressGame;
        this.status = status;
        this.reason = reason;
    }

    public static <T> NewGameResult<T> successfulResult(final T inProgressGame) {
        return new NewGameResult<>(inProgressGame, GameCreationStatus.CREATED, "");
    }

    public static <T> NewGameResult<T> failedResult(final String reason) {
        return new NewGameResult<>(null, GameCreationStatus.FAILED, reason);
    }

    public static <T> NewGameResult<T> inProgressResult(final T inProgressGame) {
        return new NewGameResult<>(inProgressGame, GameCreationStatus.IN_PROGRESS, "Game already in progress");
    }
}
