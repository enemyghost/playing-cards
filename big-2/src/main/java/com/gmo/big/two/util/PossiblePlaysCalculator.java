package com.gmo.big.two.util;

import com.gmo.big.two.Big2HandComparator;
import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.CardRank;
import com.gmo.playing.cards.Suit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PossiblePlaysCalculator {
    /**
     * Finds all valid Big2 Plays for the given collection of {@code Card}, partitioned by the number of cards in the
     * play.
     *
     * If the given collection is empty, an empty map is returned.
     *
     * The resulting map and all collections it holds are immutable. Every hand is sorted by card value, and every
     * collection of hands is sorted by hand value.
     *
     * @param cards collection of {@link Card} to find valid big2 hands in
     *
     * @return all valid Big2 Plays for the given {@code cards}, partitioned by the number of cards in the play,
     *          sorted by hand value
     */
    @SuppressWarnings("UnstableApiUsage")
    public static Map<Integer, List<List<Card>>> getPossiblePlaysByCardCount(final Collection<Card> cards) {
        if (cards.isEmpty()) {
            return Collections.emptyMap();
        }

        final List<List<Card>> possiblePlays = new ArrayList<>();
        final Map<Suit, List<Card>> cardsBySuit = new HashMap<>();
        final Map<CardRank, List<Card>> cardsByRank = new HashMap<>();
        final List<List<Card>> potentialStraights = new ArrayList<>();
        CardRank lastRank = null;
        potentialStraights.add(new ArrayList<>());
        for (final Card currentCard : cards.stream().sorted().collect(Collectors.toList())) {
            possiblePlays.add(Collections.singletonList(currentCard));
            cardsByRank.computeIfAbsent(currentCard.getRank(), (r) -> new ArrayList<>()).add(currentCard);
            cardsBySuit.computeIfAbsent(currentCard.getSuit(), (r) -> new ArrayList<>()).add(currentCard);

            // Keep track of straights
            if (lastRank != null) {
                final int rankDiff = currentCard.getRank().getRank() - lastRank.getRank();
                if (rankDiff > 1) {
                    potentialStraights.clear();
                    potentialStraights.add(Lists.newArrayList(currentCard));
                } else if (rankDiff == 1) {
                    potentialStraights.forEach(s -> {
                        if (s.size() == 5) {
                            s.remove(0);
                        }
                        s.add(currentCard);
                    });
                } else if (rankDiff == 0) {
                    potentialStraights.addAll(potentialStraights.stream()
                            .map(straightBuilder -> replaceSameRank(currentCard, straightBuilder))
                            .collect(Collectors.toList()));
                }
            } else {
                // add first card when lastRank is null
                potentialStraights.forEach(s -> s.add(currentCard));
            }
            potentialStraights.stream()
                    .filter(s -> s.size() == 5)
                    .forEach(s -> possiblePlays.add(ImmutableList.copyOf(s)));
            lastRank = currentCard.getRank();
        }
        final List<List<Card>> pairs = new ArrayList<>();
        final List<List<Card>> trips = new ArrayList<>();
        final List<List<Card>> quads = new ArrayList<>();
        cardsByRank.forEach((rank, rankList) -> {
            if (rankList.size() > 1) {
                pairs.addAll(Sets.combinations(new HashSet<>(rankList), 2).stream()
                        .map(ArrayList::new)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf)));
            }
            if (rankList.size() > 2) {
                trips.addAll(Sets.combinations(new HashSet<>(rankList), 3).stream()
                        .map(ArrayList::new)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf)));
            }
            if (rankList.size() > 3) {
                quads.add(ImmutableList.copyOf(rankList));
            }
        });

        possiblePlays.addAll(pairs);
        possiblePlays.addAll(trips);

        // Add Full Houses
        if (trips.size() > 0 && pairs.size() > 0) {
            trips.forEach(t -> possiblePlays.addAll(pairs.stream()
                    .filter(p -> !p.get(0).getRank().equals(t.get(0).getRank()))
                    .map(p -> ImmutableList.<Card>builder().addAll(t).addAll(p).build())
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf))));
        }

        // Add flushes (possibly straight)
        cardsBySuit.values().stream()
                .filter(l -> l.size() >= 5)
                .forEach(suitList ->
                        possiblePlays.addAll(Sets.combinations(new HashSet<>(suitList), 5)
                                .stream()
                                .map(ArrayList::new)
                                .peek(Collections::sort)
                                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf)))
                );

        // Add Quads
        if (quads.size() > 0) {
            cards.forEach(c -> possiblePlays.addAll(quads.stream()
                    .filter(q -> !q.get(0).getRank().equals(c.getRank()))
                    .map(q -> ImmutableList.<Card>builder().addAll(q).add(c).build())
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf))));
        }

        // Sort the possible plays, remove duplicates, and group by size
        return possiblePlays.stream()
                .sorted(Big2HandComparator.INSTANCE)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(List::size), ImmutableMap::copyOf));
    }

    /**
     * Creates a copy of list {@code s} with every occurrence of the given {@link Card}'s rank replaced by the
     * {@link Card} itself
     *
     * @param c {@link Card} to use as replacement
     * @param s {@link List} to find other cards of the same rank
     *
     * @return copy of {@code s} with the appropriate cards replaced
     */
    private static List<Card> replaceSameRank(final Card c, final List<Card> s) {
        final List<Card> copy = Lists.newArrayList(s);
        copy.replaceAll(existingCard -> {
            if (existingCard.getRank().equals(c.getRank())) {
                return c;
            }
            return existingCard;
        });
        return copy;
    }
}
