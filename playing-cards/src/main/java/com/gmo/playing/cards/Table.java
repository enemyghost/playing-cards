package com.gmo.playing.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * A table on which games of type {@link T} are played.
 *
 * @author tedelen
 */
public class Table<T> {
    private final UUID id;
    private final String name;
    private final int capacity;
    private final List<Player> players;
    private final Function<List<Player>, T> gameSupplier;
    private T currentGame;

    private Table(final Builder<T> builder) {
        id = builder.id;
        name = builder.name;
        capacity = builder.capacity;
        gameSupplier = builder.gameSupplier;
        currentGame = builder.currentGame;

        players = new ArrayList<>(capacity);
        IntStream.range(0, capacity).forEach(i -> players.add(null));

        for (final Map.Entry<Integer, Player> seatPlayer : builder.players.entrySet()) {
            players.add(seatPlayer.getKey(), seatPlayer.getValue());
        }
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Starts a new game if one is not already in progress;
     *
     * @return the created {@link T}, or the current one if one is in progress; {@link Optional#empty()} if the game cannot be created
     */
    public synchronized NewGameResult newGame() {
        if (currentGame == null) {
            if (players.size() != capacity) {
                return NewGameResult.failedResult("Table must be full to start a new game");
            }
            currentGame = gameSupplier.apply(players);
            return NewGameResult.successfulResult(currentGame);
        }

        return  NewGameResult.inProgressResult(currentGame);
    }

    /**
     * Gets the current game
     *
     * @return the current {@link T}, or {@link Optional#empty()} if none is in progress
     */
    public synchronized Optional<T> currentGame() {
        return Optional.ofNullable(currentGame);
    }

    /**
     * Adds {@link Player} to the table in the first available seat
     *
     * @param p {@link Player} to add to the table
     *
     * @return the seat number of the player, or {@link OptionalInt#empty()} if the player could not be added.
     */
    public synchronized OptionalInt addPlayer(final Player p) {
        for (int i = 0; i < capacity; i++) {
            if (players.get(i) != null) {
                if (!addPlayer(p, i)) {
                    return OptionalInt.empty();
                }
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Adds {@link Player} to the table in the given seat, if it is available.
     *
     * @param p {@link Player} to add to the table
     * @param seat seat number to seat the player
     *
     * @return true if the player was successfully added to the seat, false otherwise.
     */
    public synchronized boolean addPlayer(final Player p, final int seat) {
        if (players.size() >= capacity) {
            return false;
        } else if (players.get(seat) != null) {
            return false;
        }
        players.add(seat, p);
        return true;
    }

    public synchronized boolean removePlayer(final Player p) {
        return players.remove(p);
    }

    public synchronized Player removePlayer(final int seat) {
        return players.remove(seat);
    }

    public synchronized Player getPlayer(final int seat) {
        return players.get(seat);
    }

    public synchronized Map<Integer, Player> getPlayers() {
        final ImmutableMap.Builder<Integer, Player> playerMap = ImmutableMap.builder();
        for (int i = 0; i < capacity; i++) {
            if (players.get(i) != null) {
                playerMap.put(i, players.get(i));
            }
        }
        return playerMap.build();
    }

    public synchronized boolean isFull() {
        return players.size() == capacity;
    }

    public synchronized boolean occupied(final int seat) {
        return players.get(seat) != null;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public static final class Builder<T> {
        private UUID id;
        private String name;
        private int capacity;
        private Map<Integer, Player> players;
        private Function<List<Player>, T> gameSupplier;
        private T currentGame;

        private Builder() {
            players = new HashMap<>();
        }

        public Builder withId(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withCapacity(final int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder withPlayers(final Map<Integer, Player> players) {
            this.players = players;
            return this;
        }

        public Builder withGameSupplier(final Function<List<Player>, T> gameSupplier) {
            this.gameSupplier = gameSupplier;
            return this;
        }

        public Builder withCurrentGame(final T currentGame) {
            this.currentGame = currentGame;
            return this;
        }

        public Table build() {
            Objects.requireNonNull(gameSupplier, "Null game supplier");
            Objects.requireNonNull(players, "Null players map");
            Objects.requireNonNull(id, "Null id");
            Objects.requireNonNull(Strings.emptyToNull(name), "Null/empty name");
            Preconditions.checkArgument(capacity > 0, "Capacity must be positive");

            return new Table<>(this);
        }
    }
}
