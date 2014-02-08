package com.erigir.lamark.selector;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ISelector;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a selector that works on tournament selection.
 * <p/>
 * Pairs of individuals are chosen from the source, with the better of the
 * two being selected.
 *
 * @author cweiss
 * @since 11/2007
 */
public class Tournament extends AbstractLamarkComponent implements ISelector {

    /**
     * @see com.erigir.lamark.ISelector#select(java.util.List, int)
     */
    public List<Individual<?>> select(List<Individual<?>> individuals, int count) {
        ArrayList<Individual<?>> rval = new ArrayList<Individual<?>>(count);

        int size = individuals.size();
        for (int i = 0; i < count; i++) {
            Individual<?> first = individuals.get(getLamark().getRandom().nextInt(size));
            Individual<?> second = individuals.get(getLamark().getRandom().nextInt(size));
            if (first.getFitness().compareTo(second.getFitness()) > 0) {
                rval.add(first);
            } else {
                rval.add(second);
            }
        }

        return rval;
    }

}
