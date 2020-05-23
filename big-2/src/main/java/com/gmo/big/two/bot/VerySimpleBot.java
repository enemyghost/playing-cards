package com.gmo.big.two.bot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gmo.big.two.Big2HandComparator;
import com.gmo.big.two.Big2Play;
import com.gmo.big.two.util.PossiblePlaysCalculator;
import com.gmo.playing.cards.HandView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * A very simple bot that always plays the smallest hand possible from its hand.
 */
@JsonSerialize
@JsonDeserialize
public class VerySimpleBot implements Big2Bot {
    public static final UUID BOT_UUID = UUID.fromString("50e946fd-8c6e-4d9b-9550-2382039e4ad9");

    @JsonCreator
    public VerySimpleBot() {}
    /**
     * Returns the lowest ranked play available that beats {@code playToBeat}
     */
    @Override
    public Big2Play play(final HandView myHand, final Big2Play playToBeat) {
        return Big2Play.newBuilder()
                .withPlayer(myHand.getPlayer())
                .withHand(PossiblePlaysCalculator.getPossiblePlaysByCardCount(myHand.getCards())
                        .getOrDefault(playToBeat.getHand().size(), new ArrayList<>())
                        .stream()
                        .filter(h -> Big2HandComparator.INSTANCE.compare(h, playToBeat.getHand()) > 0)
                        .findFirst()
                        .orElse(Collections.emptyList()))
                .build();
    }

    /**
     * Returns the lowest ranked play available
     */
    @Override
    public Big2Play play(final HandView myHand) {
        return Big2Play.newBuilder()
                .withPlayer(myHand.getPlayer())
                .withHand(PossiblePlaysCalculator.getPossiblePlaysByCardCount(myHand.getCards())
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .findFirst()
                        .orElse(Collections.emptyList()))
                .build();
    }
}
