package com.gmo.big.two;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.HandView;
import com.gmo.playing.cards.Player;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Big2GameView {
    public enum GameState {
        WAITING_FOR_PLAYERS,
        OPEN,
        FORCED,
        COMPLETED
    }

    private final Player gameViewOwner;
    private final GameState gameState;
    private final List<HandView> handViews;
    private final Player nextToPlay;
    private final List<Big2Play> lastPlays;
    private final UUID gameId;
    private final UUID nextGameId;
    private final Map<UUID, Integer> scores;

    private Big2GameView(final Builder builder) {
        gameViewOwner = builder.gameViewOwner;
        gameState = builder.gameState;
        handViews = builder.handViews.build();
        nextToPlay = builder.nextToPlay;
        lastPlays = builder.lastPlays.build();
        gameId = builder.gameId;
        nextGameId = builder.nextGameId;
        scores = builder.scores;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<HandView> getHandViews() {
        return handViews;
    }

    @JsonIgnore
    public HandView nextPlayerHand() {
        return handViews.stream().filter(t->t.getPlayer().equals(getNextToPlay())).findFirst()
                .orElseThrow(() -> new IllegalStateException("no next player hand available"));
    }

    public Player getNextToPlay() {
        return nextToPlay;
    }

    public List<Big2Play> getLastPlays() {
        return lastPlays;
    }

    @JsonIgnore
    public Optional<Big2Play> playToBeat() {
        if (gameState == GameState.OPEN) {
            return Optional.empty();
        } else {
            return getLastPlays().stream().filter(t -> !t.isPass()).findFirst();
        }
    }

    public Player getGameViewOwner() {
        return gameViewOwner;
    }

    public UUID getGameId() { return gameId; }

    public UUID getNextGameId() { return nextGameId; }

    public Map<UUID, Integer> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("gameViewOwner", gameViewOwner)
                .add("gameState", gameState)
                .add("handViews", handViews)
                .add("nextToPlay", nextToPlay)
                .add("lastPlays", lastPlays)
                .add("gameId", gameId)
                .add("nextGameId", nextGameId)
                .add("scores", scores)
                .toString();
    }

    public static final class Builder {
        private Player gameViewOwner;
        private GameState gameState;
        private ImmutableList.Builder<HandView> handViews;
        private Player nextToPlay;
        private ImmutableList.Builder<Big2Play> lastPlays;
        private UUID gameId;
        private UUID nextGameId;
        private Map<UUID, Integer> scores;

        private Builder() {
            handViews = ImmutableList.builder();
            lastPlays = ImmutableList.builder();
            scores = Collections.emptyMap();
        }

        public Builder withGameId(final UUID gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder withNextGameId(final UUID nextGameId) {
            this.nextGameId = nextGameId;
            return this;
        }

        public Builder withGameState(final GameState gameState) {
            this.gameState = gameState;
            return this;
        }

        public Builder addHandView(final HandView handView) {
            handViews.add(handView);
            return this;
        }

        public Builder withNextToPlay(final Player nextToPlay) {
            this.nextToPlay = nextToPlay;
            return this;
        }

        public Builder addLastPlay(final Big2Play lastPlay) {
            lastPlays.add(lastPlay);
            return this;
        }

        public Builder withGameViewOwner(final Player gameViewOwner) {
            this.gameViewOwner = gameViewOwner;
            return this;
        }

        public Builder withScores(final Map<Player, Integer> scores) {
            this.scores = requireNonNull(scores, "Null map").entrySet()
                    .stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().getId(), Entry::getValue));
            return this;
        }

        public Big2GameView build() {
            requireNonNull(gameId);
            requireNonNull(gameState, "Null game state");
            requireNonNull(gameViewOwner, "Null owner");
            return new Big2GameView(this);
        }
    }
}
