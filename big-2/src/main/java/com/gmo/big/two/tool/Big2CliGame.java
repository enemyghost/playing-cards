package com.gmo.big.two.tool;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gmo.big.two.Big2DeckFactory;
import com.gmo.big.two.Big2Exception;
import com.gmo.big.two.Big2Game;
import com.gmo.big.two.Big2GameView;
import com.gmo.big.two.Big2GameView.GameState;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.Player;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * @author tedelen
 */
public class Big2CliGame {
    private static final Splitter COMMA_SPLITTER = Splitter.on(",");

    public static void main(String[] args) {
        final List<Player> players = ImmutableList.of(
                Player.newBuilder().withId(UUID.randomUUID()).withName("tyler").build(),
//                Player.newBuilder().withId(UUID.randomUUID()).withName("spenser").build(),
                Player.newBuilder().withId(UUID.randomUUID()).withName("haolan").isBot().build()
//                Player.newBuilder().withId(UUID.randomUUID()).withName("william").build()
        );

        final Big2Game game = Big2Game.newGame(UUID.randomUUID(), players);
        final Scanner scanner = new Scanner(System.in);

        while (!GameState.COMPLETED.equals(game.getState())) {
            final Big2GameView big2GameView = game.gameViewForPlayer(game.nextToPlay());
            big2GameView.getLastPlays().stream().findFirst().ifPresent(p ->
                    System.out.println(String.format("%s played %s", p.getPlayer().getName(), p.getHand().stream().map(Card::toString).collect(Collectors.joining(", ")))));
            System.out.println(String.format("%s's hand: %s", game.nextToPlay().getName(), big2GameView.getHandViews()
                    .stream()
                    .filter(t->t.getPlayer().getId().equals(game.nextToPlay().getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find player state"))
                    .getCards()
                    .stream()
                    .map(Card::toString)
                    .collect(Collectors.joining(", "))));
            System.out.println("What's your play? (enter 1-5 cards, e.g. \"9h, Th, Jh, Qh, Kh\"), or \"pass\": ");
            final String userHand = scanner.nextLine();
            if (userHand.trim().isEmpty()) {
                System.out.println("Enter a valid play.");
                continue;
            }

            try {
                final List<Card> play = userHand.equalsIgnoreCase("pass")
                    ? Collections.emptyList()
                    : COMMA_SPLITTER
                            .splitToList(userHand)
                            .stream()
                            .map(String::trim)
                            .filter(t -> !t.isEmpty())
                            .map(t -> {
                                final String cardName = t.startsWith("T") ? "10" : Character.toString(t.charAt(0));
                                return Big2DeckFactory.getByAbbrev(cardName, Character.toString(t.charAt(1)));
                            })
                            .collect(Collectors.toList());
                game.play(game.nextToPlay(), play);
            } catch (final Big2Exception e) {
                System.out.println(e.getMessage());
            } catch (final Exception e) {
                System.out.println("Enter a valid play.");
            }
        }

        System.out.println(game.gameViewForPlayer(game.nextToPlay()));
    }
}
