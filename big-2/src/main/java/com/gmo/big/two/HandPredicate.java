package com.gmo.big.two;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

import com.gmo.playing.cards.Card;

/**
 * @author tedelen
 */
public interface HandPredicate extends Predicate<Collection<Card>>, Comparator<Collection<Card>> { }
