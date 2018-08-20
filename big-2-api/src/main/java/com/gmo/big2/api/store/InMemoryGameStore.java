package com.gmo.big2.api.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;

/**
 * @author tedelen
 */
public class InMemoryGameStore implements GameStore {
    private final Map<UUID, Big2Game> activeGames;
    private final Map<UUID, Big2GameLobby> activeLobbies;

    public InMemoryGameStore() {
        activeGames = new HashMap<>();
        activeLobbies = new HashMap<>();
    }

    @Override
    public Optional<Big2Game> getGame(final UUID uuid) {
        return Optional.ofNullable(activeGames.get(uuid));
    }

    @Override
    public Big2Game updateGame(final Big2Game game) {
        return activeGames.put(game.getId(), game);
    }

    @Override
    public Optional<Big2GameLobby> getGameLobby(final UUID uuid) {
        return Optional.ofNullable(activeLobbies.get(uuid));
    }

    @Override
    public Big2GameLobby newLobby() {
        final Big2GameLobby big2GameLobby = Big2GameLobby.newBuilder().build();
        activeLobbies.put(big2GameLobby.getGameId(), big2GameLobby);
        return big2GameLobby;
    }

    @Override
    public Big2GameLobby updateGameLobby(final Big2GameLobby lobby) {
        if (lobby.isActive()) {
            activeLobbies.put(lobby.getGameId(), lobby);
        } else {
            activeLobbies.remove(lobby.getGameId());
        }
        return lobby;
    }

    @Override
    public Big2Game startGame(final UUID uuid) {
        if (activeLobbies.containsKey(uuid)) {
            final Big2Game start = activeLobbies.get(uuid).start();
            activeLobbies.remove(uuid);
            activeGames.put(start.getId(), start);
            return start;
        }

        throw new IllegalStateException("Cannot start game for given UUID");
    }
}
