package com.gmo.big.two;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.playing.cards.Player;
import com.google.common.collect.ImmutableList;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Big2GameLobby.Builder.class)
public class Big2GameLobby {
    private final List<Player> players;
    private final UUID gameId;
    private final AtomicBoolean sealed;

    private Big2GameLobby(final Builder builder) {
        players = builder.players;
        gameId = builder.gameId;
        sealed = builder.sealed;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Big2Game start() {
        if (sealed.get()) {
            throw new IllegalStateException("You cannot start a new game, it's already started");
        } else if (players.size() < 2) {
            throw new IllegalStateException("You cannot start a game with fewer than 2 players");
        } else if (players.stream().allMatch(Player::isBot)) {
            throw new IllegalStateException("You must have at least one human to start a game");
        }
        sealed.set(true);
        return Big2Game.newGame(gameId, players);
    }

    public List<Player> getPlayers() {
        return ImmutableList.copyOf(players);
    }

    public boolean addPlayer(final Player p) {
        if (!sealed.get() && players.stream().noneMatch(t->t.getId().equals(p.getId())) && players.size() < 4) {
            return players.add(p);
        }
        return false;
    }

    public boolean removePlayer(final Player p) {
        return players.removeIf(t -> t.getId().equals(p.getId()));
    }

    public int playerCount() {
        return players.size();
    }

    public UUID getGameId() {
        return gameId;
    }

    public boolean isActive() { return !sealed.get(); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private List<Player> players;
        private UUID gameId;
        private AtomicBoolean sealed;

        private Builder() {
            gameId = UUID.randomUUID();
            players = new ArrayList<>(4);
            sealed = new AtomicBoolean(false);
        }

        public Builder withPlayers(final List<Player> players) {
            this.players = new ArrayList<>(players);
            return this;
        }

        public Builder withGameId(final UUID gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder withSealed(final AtomicBoolean sealed) {
            this.sealed = sealed;
            return this;
        }

        public Big2GameLobby build() {
            return new Big2GameLobby(this);
        }
    }
}
