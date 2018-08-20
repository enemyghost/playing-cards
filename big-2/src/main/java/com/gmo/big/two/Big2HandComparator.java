package com.gmo.big.two;

import static com.gmo.big.two.Big2HandComparator.FlushPredicate.FLUSH_PREDICATE;
import static com.gmo.big.two.Big2HandComparator.FourOfAKindPredicate.FOUR_OF_A_KIND_PREDICATE;
import static com.gmo.big.two.Big2HandComparator.FullHousePredicate.FULL_HOUSE_PREDICATE;
import static com.gmo.big.two.Big2HandComparator.StraightFlushPredicate.STRAIGHT_FLUSH_PREDICATE;
import static com.gmo.big.two.Big2HandComparator.StraightPredicate.STRAIGHT_PREDICATE;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gmo.playing.cards.Card;
import com.gmo.playing.cards.CardRank;
import com.gmo.playing.cards.CardRank.CardName;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

/**
 * Comparator for hands using big 2 rankings
 *
 * @author tedelen
 */
public class Big2HandComparator implements Comparator<Collection<Card>> {
    static final Big2HandComparator INSTANCE = new Big2HandComparator();

    private static final List<HandPredicate> ORDERED_PREDICATES = ImmutableList.of(
            STRAIGHT_FLUSH_PREDICATE,
            FOUR_OF_A_KIND_PREDICATE,
            FULL_HOUSE_PREDICATE,
            FLUSH_PREDICATE,
            STRAIGHT_PREDICATE,
            new MatchPredicate(3),
            new MatchPredicate(2),
            new MatchPredicate(1)
    );

    @Override
    public int compare(final Collection<Card> o1, final Collection<Card> o2) {
        if (o1.isEmpty()) {
            return o2.isEmpty() ? 0 : -1;
        } else if (o2.isEmpty()) {
            return 1;
        }

        for (final HandPredicate hp : ORDERED_PREDICATES) {
            final boolean o1Matches = hp.test(o1);
            final boolean o2Matches = hp.test(o2);
            if (o1Matches && !o2Matches) {
                return 1;
            } else if (o2Matches && !o1Matches) {
                return -1;
            } else if (o1Matches && o2Matches) {
                return hp.compare(o1, o2);
            }
        }
        return 0;
    }

    public static boolean isValidBig2Hand(final Collection<Card> nextPlay) {
        return ORDERED_PREDICATES.stream().anyMatch(t -> t.test(nextPlay));
    }

    static class MatchPredicate implements HandPredicate {
        private final int numCards;
        private MatchPredicate(final int numCards) {
            Preconditions.checkArgument(numCards > 0 && numCards <= 3, "Num cards must be 1-3");
            this.numCards = numCards;
        }

        @Override
        public boolean test(final Collection<Card> cards) {
            return cards.size() == numCards
                    && cards.stream().map(Card::getRank).distinct().count() == 1;
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                return o1.stream().max(Ordering.natural()).orElseThrow(() -> new IllegalStateException("not a hand"))
                        .compareTo(o2.stream().max(Ordering.natural()).orElseThrow(() -> new IllegalStateException("not a hand")));
            }

            return 0;
        }
    }

    static class FlushPredicate implements HandPredicate {
        static final FlushPredicate FLUSH_PREDICATE = new FlushPredicate();

        @Override
        public boolean test(final Collection<Card> cards) {
            return cards.size() == 5
                    && cards.stream().map(c -> c.getSuit().getSuitName()).distinct().count() == 1;
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                final List<Integer> sorted1 = o1.stream()
                        .map(c -> c.getRank().getRank())
                        .sorted(Ordering.natural().reverse())
                        .collect(Collectors.toList());
                final List<Integer> sorted2 = o2.stream()
                        .map(c -> c.getRank().getRank())
                        .sorted(Ordering.natural().reverse())
                        .collect(Collectors.toList());
                for (int i = 0; i < 5; i++) {
                    if (!sorted1.get(i).equals(sorted2.get(i))) {
                        return sorted1.get(i) - sorted2.get(i);
                    }
                }

                return o1.stream().findFirst().map(t -> t.getSuit().getSuitValue()).orElse(-1)
                        .compareTo(o2.stream().findFirst().map(t -> t.getSuit().getSuitValue()).orElse(-1));
            }

            return 0;
        }
    }

    static class StraightPredicate implements HandPredicate {
        static final StraightPredicate STRAIGHT_PREDICATE = new StraightPredicate();

        private static final List<CardRank> SPECIAL_STRAIGHT = ImmutableList.of(
                CardRank.newBuilder().withCardName(CardName.THREE).withRank(3).build(),
                CardRank.newBuilder().withCardName(CardName.FOUR).withRank(4).build(),
                CardRank.newBuilder().withCardName(CardName.FIVE).withRank(5).build(),
                CardRank.newBuilder().withCardName(CardName.SIX).withRank(6).build(),
                CardRank.newBuilder().withCardName(CardName.TWO).withRank(15).build()
        );

        private static final List<CardRank> WHEEL = ImmutableList.of(
                CardRank.newBuilder().withCardName(CardName.THREE).withRank(3).build(),
                CardRank.newBuilder().withCardName(CardName.FOUR).withRank(4).build(),
                CardRank.newBuilder().withCardName(CardName.FIVE).withRank(5).build(),
                CardRank.newBuilder().withCardName(CardName.ACE).withRank(14).build(),
                CardRank.newBuilder().withCardName(CardName.TWO).withRank(15).build()
        );

        @Override
        public boolean test(final Collection<Card> cards) {
            if (cards.size() != 5) {
                return false;
            }
            final List<CardRank> sortedByRank = cards.stream()
                    .map(Card::getRank)
                    .sorted(Comparator.comparingInt(CardRank::getRank))
                    .collect(Collectors.toList());
            if (sortedByRank.equals(SPECIAL_STRAIGHT) || sortedByRank.equals(WHEEL)) {
                return true;
            }
            for (int i = 1; i < 5; i++) {
                if (sortedByRank.get(i - 1).getRank() + 1 != sortedByRank.get(i).getRank()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                final List<Card> o1sorted = o1.stream()
                        .sorted(Comparator.<Card>comparingInt(c -> c.getRank().getRank()).reversed())
                        .collect(Collectors.toList());
                final List<Card> o2sorted = o2.stream()
                        .sorted(Comparator.<Card>comparingInt(c -> c.getRank().getRank()).reversed())
                        .collect(Collectors.toList());

                // Special logic for wheel vs. special straight
                if (o1sorted.stream().map(Card::getRank).collect(Collectors.toList()).equals(WHEEL)
                        && o2sorted.stream().map(Card::getRank).collect(Collectors.toList()).equals(SPECIAL_STRAIGHT)) {
                    return -1;
                } else if (o2sorted.stream().map(Card::getRank).collect(Collectors.toList()).equals(WHEEL)
                        && o1sorted.stream().map(Card::getRank).collect(Collectors.toList()).equals(SPECIAL_STRAIGHT)) {
                    return 1;
                }

                for (int i = 0; i < 5; i++) {
                    if (o1sorted.get(i).getRank().getRank() != o2sorted.get(i).getRank().getRank()) {
                        return o1sorted.get(i).getRank().getRank() - o2sorted.get(i).getRank().getRank();
                    }
                }

                return o1sorted.get(0).getSuit().compareTo(o2sorted.get(0).getSuit());
            }

            return 0;
        }
    }

    static class FullHousePredicate implements HandPredicate {
        static final FullHousePredicate FULL_HOUSE_PREDICATE = new FullHousePredicate();

        private static final ImmutableSet<Long> FULL_HOUSE_COUNTS = ImmutableSet.of(2L, 3L);

        @Override
        public boolean test(final Collection<Card> cards) {
            if (cards.size() != 5) {
                return false;
            }
            final Map<CardRank, Long> countByRank = cards.stream()
                    .map(Card::getRank)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            if (countByRank.keySet().size() != 2) {
                return false;
            }
            return new HashSet<>(countByRank.values()).equals(FULL_HOUSE_COUNTS);
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                final CardRank dominantRank1 = o1.stream()
                        .map(Card::getRank)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(cr -> cr.getValue() == 3)
                        .map(Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Not a full house"));
                final CardRank dominantRank2 = o2.stream()
                        .map(Card::getRank)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(cr -> cr.getValue() == 3)
                        .map(Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Not a full house"));
                return dominantRank1.compareTo(dominantRank2);
            }
            return 0;
        }
    }

    static class FourOfAKindPredicate implements HandPredicate {
        static final FourOfAKindPredicate FOUR_OF_A_KIND_PREDICATE = new FourOfAKindPredicate();

        private static final ImmutableSet<Long> FOUR_OF_A_KIND_COUNTS = ImmutableSet.of(4L, 1L);

        @Override
        public boolean test(final Collection<Card> cards) {
            if (cards.size() != 5) {
                return false;
            }
            final Map<CardRank, Long> countByRank = cards.stream()
                    .map(Card::getRank)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            return new HashSet<>(countByRank.values()).equals(FOUR_OF_A_KIND_COUNTS);
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                final CardRank dominantRank1 = o1.stream()
                        .map(Card::getRank)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(cr -> cr.getValue() == 4)
                        .map(Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Not quads"));
                final CardRank dominantRank2 = o2.stream()
                        .map(Card::getRank)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(cr -> cr.getValue() == 4)
                        .map(Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Not quads"));
                return dominantRank1.compareTo(dominantRank2);
            }
            return 0;
        }
    }

    static class StraightFlushPredicate implements HandPredicate {
        static final StraightFlushPredicate STRAIGHT_FLUSH_PREDICATE = new StraightFlushPredicate();

        @Override
        public boolean test(final Collection<Card> cards) {
            return FLUSH_PREDICATE.test(cards) && STRAIGHT_PREDICATE.test(cards);
        }

        @Override
        public int compare(final Collection<Card> o1, final Collection<Card> o2) {
            if (test(o1) && test(o2)) {
                return FLUSH_PREDICATE.compare(o1, o2);
            }
            return 0;
        }
    }
}
