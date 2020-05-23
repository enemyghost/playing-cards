package com.gmo.big2.api.controller;

import com.gmo.big.two.Big2Exception;
import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameLobby;
import com.gmo.big.two.Big2GameView;
import com.gmo.big.two.Big2GameView.GameState;
import com.gmo.big.two.bot.VerySimpleBot;
import com.gmo.big2.api.servlet.ServletAuthUtils;
import com.gmo.big2.api.store.GameStore;
import com.gmo.big2.api.support.GameResultsCsvResponseWriter;
import com.gmo.big2.auth.entities.User;
import com.gmo.big2.store.game.CompletedGameStore;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.GameResults;
import com.gmo.playing.cards.HandView;
import com.gmo.playing.cards.LeaderboardEntry;
import com.gmo.playing.cards.Player;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author tedelen
 */
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = {"http://c6db7663.ngrok.io", "https://whispering-ocean-60773.herokuapp.com", "http://localhost:3000" }, allowCredentials = "true")
public class Big2GameController {
    private final GameStore gameStore;
    private final CompletedGameStore completedGameStore;

    public Big2GameController(final GameStore gameStore, final CompletedGameStore completedGameStore) {
        this.gameStore = Objects.requireNonNull(gameStore, "Null game store");
        this.completedGameStore = Objects.requireNonNull(completedGameStore, "Null completed game store");
    }

    @MessageMapping("/games/push/{gameId}")
    @SendTo("/topic/games/{gameId}")
    public UUID pushUpdate(@DestinationVariable final UUID gameId) {
        System.out.println("Got update");
        return gameId;
    }

    @PostMapping(value = "/games")
    public ResponseEntity<UUID> createGame(final HttpServletRequest request) {
        final Player player = getPlayer(request);
        final Big2GameLobby big2GameLobby = gameStore.newLobby();
        big2GameLobby.addPlayer(player);

        return ResponseEntity.ok(big2GameLobby.getGameId());
    }

    @PostMapping(value = "/games/{gameUuid}/newGame", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<UUID> createGame(final HttpServletRequest request,
                                           @PathVariable final UUID gameUuid) {
        final Player player = getPlayer(request);
        final Optional<Big2Game> game = gameStore.getGame(gameUuid);
        if (game.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        final Big2Game big2Game = game.get();
        if (!big2Game.isCompleted()) {
            return ResponseEntity.badRequest().build();
        } else if (big2Game.getNextGameId().isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        final List<Player> players = big2Game.getPlayers();
        final Player winner = big2Game.getWinner().orElseThrow(() -> new RuntimeException("ain't no winner"));
        if (!player.equals(winner) && !winner.isBot()) {
            return ResponseEntity.badRequest().build();
        }

        final Big2GameLobby big2GameLobby = gameStore.newLobby();
        big2Game.setNextGameId(big2GameLobby.getGameId());
        gameStore.updateGame(big2Game);

        int currentIndex = players.indexOf(winner);
        while (big2GameLobby.playerCount() < players.size()) {
            big2GameLobby.addPlayer(players.get(currentIndex));
            currentIndex++;
            if (currentIndex >= players.size()) {
                currentIndex = 0;
            }
        }
        gameStore.updateGameLobby(big2GameLobby);
        final Big2Game newBig2Game = big2GameLobby.start();
        gameStore.startGame(newBig2Game.getId());
        gameStore.updateGame(newBig2Game);
        return ResponseEntity.ok(newBig2Game.getId());
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
        if (gameStore.getGameLobby(gameUuid).isEmpty()) {
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
        final Optional<Big2Game> big2GameOpt = gameStore.getGame(gameUuid);
        if (big2GameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Player player = getPlayer(request);
        final Big2Game big2Game = big2GameOpt.get();
        try {
            big2Game.play(player, cardsToPlay);
        } catch (final Big2Exception exception) {
            return ResponseEntity.badRequest().body(big2Game.gameViewForPlayer(player));
        }
        gameStore.updateGame(big2Game);
        if (big2Game.isCompleted()) {
            completedGameStore.saveFinalGameState(big2Game);
        }
        return ResponseEntity.ok(big2Game.gameViewForPlayer(player));
    }

    @PostMapping(value = "/games/{gameUuid}/players")
    public ResponseEntity<Big2GameView> joinGame(final HttpServletRequest request,
                                                 @PathVariable final UUID gameUuid,
                                                 @RequestParam(required = false, defaultValue = "false") final boolean isBot) {
        final Optional<Big2GameLobby> gameLobby = gameStore.getGameLobby(gameUuid);
        if (gameLobby.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Player player = isBot
            ? Player.newBuilder().withId(VerySimpleBot.BOT_UUID).withName("not_a_bot").isBot().build()
            : getPlayer(request);
        if (isBot && gameLobby.get().getPlayers().stream().anyMatch(Player::isBot)) {
            return ResponseEntity.badRequest().build();
        }
        gameLobby.get().addPlayer(player);
        gameStore.updateGameLobby(gameLobby.get());
        return ResponseEntity.ok(gameLobbyToView(gameLobby.get(), player));
    }

    @GetMapping(value = "/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> leaderboard(final HttpServletRequest request) {
        return ResponseEntity.ok(completedGameStore.leaderboard());
    }

    @GetMapping(value = "/fullHistory", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameResults> fullHistory() {
        return ResponseEntity.ok(completedGameStore.history());
    }

    @GetMapping(value = "/fullHistory.csv", produces = "text/csv")
    public void fullHistoryCsv(final HttpServletResponse response,
                               @RequestParam(value = "flattened", required = false, defaultValue = "false") final boolean flattened) throws Exception {
        final GameResults history = completedGameStore.history();
        if (flattened) {
            GameResultsCsvResponseWriter.writeFlattenedResponse(response.getWriter(), history);
        } else {
            GameResultsCsvResponseWriter.writeWideResponse(response.getWriter(), history);
        }
    }

    private Big2GameView gameLobbyToView(final Big2GameLobby gameLobby, final Player player) {
        final Big2GameView.Builder gameView = Big2GameView.newBuilder()
                .withGameId(gameLobby.getGameId())
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
