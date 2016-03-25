package com.erigir.lamark.selector;

import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements a selector that works on tournament selection.
 * &lt;p /&gt;
 * Pairs of individuals are chosen from the source, with the better of the
 * two being selected.
 *
 * @author cweiss
 * @since 11/2007
 */
public class TournamentSelector<T> implements Selector<T> {
    public List<Individual<T>> select(List<Individual<T>> sortedList, Random random, int numberToSelect, boolean minimumBest)
    {
        List<Individual<T>> rval = new ArrayList<>(numberToSelect);

        for (int i=0;i<numberToSelect;i++)
        {
            Individual<T> c1  = sortedList.get(random.nextInt(sortedList.size()));
            Individual<T> c2  = sortedList.get(random.nextInt(sortedList.size()));
            rval.add((c1.getFitness().compareTo(c2.getFitness())>0 && !minimumBest)?c1:c2);
        }
        return rval;
    }
}
