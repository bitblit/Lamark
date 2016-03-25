package com.erigir.lamark.selector;

import com.erigir.lamark.stream.Individual;

import java.util.List;
import java.util.Random;

/**
 * Implements a selector that works on tournament selection.
 * <p/>
 * Pairs of individuals are chosen from the source, with the better of the
 * two being selected.
 *
 * @author cweiss
 * @since 11/2007
 */
public class TournamentSelector<T> implements Selector<T> {
    public Individual<T> select(List<Individual<T>> sortedList, Random random, double totalFitness)
    {
        Individual<T> c1  = sortedList.get(random.nextInt(sortedList.size()));
        Individual<T> c2  = sortedList.get(random.nextInt(sortedList.size()));

        return (c1.getFitness().compareTo(c2.getFitness())>0)?c1:c2;
    }
}
