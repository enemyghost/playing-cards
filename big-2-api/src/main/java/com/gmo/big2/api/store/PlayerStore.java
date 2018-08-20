package com.gmo.big2.api.store;

import java.util.Optional;
import java.util.UUID;

import com.gmo.playing.cards.Player;

/**
 * @author tedelen
 */
public interface PlayerStore {
    Optional<Player> getPlayer(UUID uuid);

    Optional<Player> getPlayerByTokenId(UUID uuid);

    Optional<Player> findPlayer(String name);

    UUID newPlayer(Player player);
}
