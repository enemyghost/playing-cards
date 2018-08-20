package com.gmo.big2.api.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.gmo.playing.cards.Player;

/**
 * @author tedelen
 */
public class InMemoryPlayerStore implements PlayerStore {
    private final Map<UUID, Player> allPlayers;
    private final Map<UUID, UUID> tokens;

    public InMemoryPlayerStore() {
        allPlayers = new HashMap<>();
        tokens = new HashMap<>();
    }

    @Override
    public synchronized Optional<Player> getPlayer(final UUID uuid) {
        return Optional.ofNullable(allPlayers.get(uuid));
    }

    @Override
    public synchronized Optional<Player> getPlayerByTokenId(final UUID uuid) {
        if (tokens.containsKey(uuid)) {
            return Optional.ofNullable(allPlayers.get(tokens.get(uuid)));
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Player> findPlayer(final String name) {
        return allPlayers.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public synchronized UUID newPlayer(final Player player) {
        final Player result = getPlayer(player.getId()).orElse(findPlayer(player.getName()).orElse(player));
        final UUID token = UUID.randomUUID();
        tokens.put(token, result.getId());
        allPlayers.put(player.getId(), player);
        return token;
    }
}
