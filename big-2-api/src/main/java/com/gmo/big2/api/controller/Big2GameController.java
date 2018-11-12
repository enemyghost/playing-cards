package com.gmo.big2.api.controller;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gmo.big.two.Big2Exception;
import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;
import com.gmo.big.two.Big2GameView;
import com.gmo.big.two.Big2GameView.GameState;
import com.gmo.big.two.auth.entities.User;
import com.gmo.big2.api.servlet.ServletAuthUtils;
import com.gmo.big2.api.store.GameStore;
import com.gmo.big2.api.store.PlayerStore;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.HandView;
import com.gmo.playing.cards.Player;

/**
 * @author tedelen
 */
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = { "https://whispering-ocean-60773.herokuapp.com", "http://localhost:3000" }, allowCredentials = "true")
public class Big2GameController {
    private final GameStore gameStore;

    public Big2GameController(final GameStore gameStore) {
        this.gameStore = Objects.requireNonNull(gameStore, "Null game store");
    }

    @PostMapping(value = "/games")
    public ResponseEntity<UUID> createGame(final HttpServletRequest request) {
        final Player player = getPlayer(request);
        final Big2GameLobby big2GameLobby = gameStore.newLobby();
        big2GameLobby.addPlayer(player);

        return ResponseEntity.ok(big2GameLobby.getGameId());
    }

    @GetMapping(value = "/games/{gameUuid}", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<Big2GameView> getGame(final HttpServletRequest request,
                                                @PathVariable final UUID gameUuid) {
        final Player player = getPlayer(request);
        final Optional<Big2Game> game = gameStore.getGame(gameUuid);
        if (game.isPresent()) {
            return ResponseEntity.ok(game.get().gameViewForPlayer(player));
        }

        final Optional<Big2GameLobby> gameLobby = gameStore.getGameLobby(gameUuid);
        if (gameLobby.isPresent()) {
            Big2GameView view = gameLobbyToView(gameLobby.get(), player);
            return ResponseEntity.ok(view);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/games/{gameUuid}/status/START")
    public ResponseEntity<Big2GameView> startGame(final HttpServletRequest request,
                                                  @PathVariable final UUID gameUuid) {
        if (!gameStore.getGameLobby(gameUuid).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        final Big2Game big2Game = gameStore.startGame(gameUuid);
        gameStore.updateGame(big2Game);
        return ResponseEntity.ok(big2Game.gameViewForPlayer(getPlayer(request)));
    }

    @PostMapping(value = "/games/{gameUuid}/plays")
    public ResponseEntity<Big2GameView> play(final HttpServletRequest request,
                                             @PathVariable final UUID gameUuid,
                                             @RequestBody final List<Card> cardsToPlay) {
        final Optional<Big2Game> big2Game = gameStore.getGame(gameUuid);
        if (!big2Game.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        final Player player = getPlayer(request);
        try {
            big2Game.get().play(player, cardsToPlay);
        } catch (final Big2Exception exception) {
            return ResponseEntity.badRequest().body(big2Game.get().gameViewForPlayer(player));
        }
        gameStore.updateGame(big2Game.get());
        return ResponseEntity.ok(big2Game.get().gameViewForPlayer(player));
    }

    @PostMapping(value = "/games/{gameUuid}/players")
    public ResponseEntity<Big2GameView> joinGame(final HttpServletRequest request,
                                                 @PathVariable final UUID gameUuid) {
        final Optional<Big2GameLobby> gameLobby = gameStore.getGameLobby(gameUuid);
        if (!gameLobby.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        final Player player = getPlayer(request);
        gameLobby.get().addPlayer(player);
        gameStore.updateGameLobby(gameLobby.get());
        return ResponseEntity.ok(gameLobbyToView(gameLobby.get(), player));
    }

    private Big2GameView gameLobbyToView(final Big2GameLobby gameLobby, final Player player) {
        final Big2GameView.Builder gameView = Big2GameView.newBuilder()
                .withGameState(GameState.WAITING_FOR_PLAYERS)
                .withGameViewOwner(player);

        for (int i = 0; i < gameLobby.getPlayers().size(); i++) {
            final Player p = gameLobby.getPlayers().get(i);
            gameView.addHandView(HandView.newBuilder()
                    .withPlayer(p)
                    .withPosition(i)
                    .withCardCount(0)
                    .withIsDealer(i==0)
                    .build());
        }
        return gameView.build();
    }

    private Player getPlayer(final HttpServletRequest request) {
        final User user = ServletAuthUtils.retrieveUser(request).getUser();
        return Player.newBuilder()
                .withId(user.getUserId())
                .withName(user.getDisplayName())
                .build();
    }
}
