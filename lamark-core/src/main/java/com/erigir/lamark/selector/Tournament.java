package com.erigir.lamark.selector;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.ISelector;
import com.erigir.lamark.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public class Tournament implements ISelector {
    private static final Logger LOG = LoggerFactory.getLogger(Tournament.class);

    private Random random;
    private EFitnessType fitnessType;

    /**
     * @see com.erigir.lamark.ISelector#select(java.util.List)
     */
    public Individual<?> select(List<Individual<?>> individuals) {
        Individual<?> rval = null;
        int size = individuals.size();
            Individual<?> first = individuals.get(random.nextInt(size));
            Individual<?> second = individuals.get(random.nextInt(size));

            if (first.getFitness().compareTo(second.getFitness()) > 0 && fitnessType==EFitnessType.MAXIMUM_BEST) {
                rval = first;
            } else {
                rval = second;
            }
        return rval;
    }

    public void initialize(Random random,EFitnessType fitnessType) {
        this.random = random;
        this.fitnessType = fitnessType;
    }

}
