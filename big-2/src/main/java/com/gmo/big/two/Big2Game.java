package com.gmo.big.two;

import static com.gmo.big.two.Big2HandComparator.isValidBig2Hand;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.big.two.Big2Exception.GameOverException;
import com.gmo.big.two.Big2Exception.IllegalPlayException;
import com.gmo.big.two.Big2Exception.NotYourTurnException;
import com.gmo.big.two.Big2Exception.PlayerDoesNotHaveCardsException;
import com.gmo.big.two.Big2Exception.TooManyCardsException;
import com.gmo.big.two.Big2GameView.GameState;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.Deck;
import com.gmo.playing.cards.Hand;
import com.gmo.playing.cards.HandView;
import com.gmo.playing.cards.Player;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A Big 2 game.
 *
 * @author tedelen
 */
@JsonDeserialize(builder = Big2Game.Builder.class)
public class Big2Game {
    private final UUID id;
    private final List<Player> players;
    private final Map<UUID, Hand> playerHands;
    private final Stack<Big2Play> plays;
    private final Player dealer;

    private GameState gameState;
    private AtomicInteger nextToPlay;

    private Big2Game(final Builder builder) {
        id = builder.id;
        players = builder.players;
        playerHands = builder.playerHands == null ? deal(players, Big2DeckFactory.getShuffledBig2Deck()) : builder.playerHands;
        plays = builder.plays;
        dealer = builder.dealer;
        gameState = builder.gameState;
        nextToPlay = builder.nextToPlay;
    }

    public static Big2Game newGame(final UUID uuid, final List<Player> players) {
        return Big2Game.newBuilder()
                .withId(uuid)
                .withPlayers(players)
                .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonIgnore
    public synchronized Player nextToPlay() {
        return players.get(nextToPlay.get() % players.size());
    }

    public Player getDealer() {
        return dealer;
    }

    public GameState getState() {
        return gameState;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<UUID, Hand> getPlayerHands() {
        return playerHands;
    }

    public Stack<Big2Play> getPlays() {
        return plays;
    }

    public GameState getGameState() {
        return gameState;
    }

    public AtomicInteger getNextToPlay() {
        return nextToPlay;
    }

    @JsonIgnore
    public Map<Player, Hand> getFinalHands() {
        if (!GameState.COMPLETED.equals(gameState)) {
            throw new IllegalStateException("You cannot access final hands before the game is over");
        }

        return players.stream().collect(Collectors.toMap(Function.identity(), p -> playerHands.get(p.getId())));
    }

    public UUID getId() {
        return id;
    }

    @JsonIgnore
    public synchronized boolean canPlay(final Player player, final Collection<Card> cards) {
        if (GameState.COMPLETED.equals(gameState)) {
            return false;
        } else if (!player.getId().equals(nextToPlay().getId())) {
            return false;
        } else if (!playerHands.get(player.getId()).containsCards(cards)) {
            return false;
        } else if (cards.size() > 5) {
            return false;
        }

        return isValidPlay(cards);
    }

    @JsonIgnore
    public synchronized Big2GameView play(final Player player, final Collection<Card> cards) throws Big2Exception {
        if (GameState.COMPLETED.equals(gameState)) {
            throw new GameOverException(player);
        } else if (!player.getId().equals(nextToPlay().getId())) {
            throw new NotYourTurnException(nextToPlay(), player);
        } else if (!playerHands.get(player.getId()).containsCards(cards)) {
            throw new PlayerDoesNotHaveCardsException(player);
        } else if (cards.size() > 5) {
            throw new TooManyCardsException(player);
        } else if (!isValidPlay(cards)) {
            throw new IllegalPlayException(player, plays.empty() ? Collections.emptyList() : plays.peek().getHand());
        }

        nextToPlay.incrementAndGet();
        if (cards.size() > 0) {
            if (!playerHands.get(player.getId()).removeCards(cards)) {
                throw new PlayerDoesNotHaveCardsException(player);
            }
            plays.push(Big2Play.newBuilder().withPlayer(player).withHand(cards).build());
            gameState = GameState.FORCED;
            if (playerHands.get(player.getId()).cardsRemaining() == 0) {
                gameState = GameState.COMPLETED;
            }
        } else {
            final Player nextPlayer = nextToPlay();
            if (plays.size() == 0 || nextPlayer.getId().equals(plays.peek().getPlayer().getId())) {
                gameState = GameState.OPEN;
            }
        }

        return gameViewForPlayer(player);
    }

    @JsonIgnore
    public synchronized Big2GameView gameViewForPlayer(final Player player) {
        final Big2GameView.Builder gameView = Big2GameView.newBuilder()
                .withGameState(gameState)
                .withNextToPlay(nextToPlay())
                .withGameViewOwner(player);
        if (!GameState.OPEN.equals(gameState)) {
            for (final Big2Play play : Lists.reverse(plays)) {
                gameView.addLastPlay(play);
                if (play.getPlayer().equals(player)) {
                    break;
                }
            }
        }

        final int playerOffset = players.contains(player) ? players.size() - players.indexOf(player) : 0;
        for (int i = 0; i < players.size(); i++) {
            final Player p = players.get(i);
            final boolean isDealer = i == 0;
            final boolean isHandForPlayer = p.getId().equals(player.getId());
            final HandView.Builder handViewBuilder = HandView.newBuilder()
                    .withPlayer(p)
                    .withPosition((i + playerOffset) % players.size())
                    .withIsDealer(isDealer)
                    .withIsNextToPlay((nextToPlay.get() % players.size()) == i)
                    .withCardCount(playerHands.get(p.getId()).getCards().size());

            if (isHandForPlayer || gameState.equals(GameState.COMPLETED)) {
                handViewBuilder.withCards(playerHands.get(p.getId()).getCards());
            }
            gameView.addHandView(handViewBuilder.build());
        }
        return gameView.build();
    }

    private boolean isValidPlay(final Collection<Card> nextPlay) {
        if (nextPlay.size() == 0) {
            return true;
        }
        if (!isValidBig2Hand(nextPlay)) {
            return false;
        } else if (GameState.OPEN.equals(gameState)) {
            return true;
        }

        final Big2Play lastPlay = plays.peek();
        if (lastPlay.getHand().size() != nextPlay.size()) {
            return false;
        } else {
            return Big2HandComparator.INSTANCE.compare(nextPlay, lastPlay.getHand()) > 0;
        }
    }

    private Map<UUID, Hand> deal(final List<Player> players, final Deck deck) {
        final Map<UUID, Hand> result = new HashMap<>();
        for (int i = 0; i < 13; i++) {
            for (final Player p : players) {
                result.computeIfAbsent(p.getId(), id -> new Hand(13))
                        .addCard(Iterables.getOnlyElement(deck.draw(1)));
            }
        }
        return result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private UUID id;
        private List<Player> players;
        private Map<UUID, Hand> playerHands;
        private Stack<Big2Play> plays;
        private Player dealer;
        private GameState gameState;
        private AtomicInteger nextToPlay;

        private Builder() {
            this.nextToPlay = new AtomicInteger(0);
            this.plays = new Stack<>();
            this.gameState = GameState.OPEN;
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder withPlayerHands(Map<UUID, Hand> playerHands) {
            this.playerHands = playerHands;
            return this;
        }

        public Builder withPlays(Stack<Big2Play> plays) {
            this.plays = plays;
            return this;
        }

        public Builder withDealer(Player dealer) {
            this.dealer = dealer;
            return this;
        }

        public Builder withGameState(GameState gameState) {
            this.gameState = gameState;
            return this;
        }

        public Builder withNextToPlay(AtomicInteger nextToPlay) {
            this.nextToPlay = nextToPlay;
            return this;
        }

        public Big2Game build() {
            return new Big2Game(this);
        }
    }
}
