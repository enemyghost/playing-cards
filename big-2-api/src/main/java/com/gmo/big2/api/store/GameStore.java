package com.gmo.big2.api.store;

import java.util.Optional;
import java.util.UUID;

import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;

/**
 * @author tedelen
 */
public interface GameStore {
    Optional<Big2Game> getGame(UUID uuid);

    Big2Game updateGame(Big2Game game);

    Optional<Big2GameLobby> getGameLobby(UUID uuid);

    Big2GameLobby newLobby();

    Big2GameLobby updateGameLobby(Big2GameLobby lobby);

    Big2Game startGame(UUID uuid);
}
