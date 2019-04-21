package com.gmo.big.two;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.Player;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * @author tedelen
 */
@JsonDeserialize(builder = Big2Play.Builder.class)
public class Big2Play {
    private final Player player;
    private final Collection<Card> hand;

    private Big2Play(final Builder builder) {
        this.player = builder.player;
        this.hand = ImmutableList.copyOf(builder.hand);
    }

    public static Big2Play pass(final Player player) {
        return Big2Play.newBuilder().withPlayer(player).withHand(Collections.emptyList()).build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Player getPlayer() {
        return player;
    }

    public Collection<Card> getHand() {
        return hand;
    }

    @JsonIgnore
    public boolean isPass() {
        return hand.size() == 0;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Player player;
        private Collection<Card> hand;

        private Builder() {

        }

        public Builder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public Builder withHand(Collection<Card> hand) {
            this.hand = hand;
            return this;
        }

        public Big2Play build() {
            Objects.requireNonNull(player, "Null player");
            Objects.requireNonNull(hand, "Null hand");
            Preconditions.checkArgument(hand.isEmpty() || Big2HandComparator.isValidBig2Hand(hand), "Not a valid Big2 play");
            return new Big2Play(this);
        }
    }
}
