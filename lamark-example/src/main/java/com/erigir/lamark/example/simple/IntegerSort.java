/*
 * Created on Feb 22, 2005
 */
package com.erigir.lamark.example.simple;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.FitnessType;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.Individual;

import java.util.List;

/**
 * This fitness function sorts a list of integers by multiplying the position of
 * an integer by its value.  This tends to create the largest value by putting the
 * largest numbers in the highest positions.  For simple viewing, a permutation
 * should be used instead of a generic list of integers.
 *
 * @author cweiss
 * @since 02/2005
 */
public class IntegerSort extends AbstractLamarkComponent implements IFitnessFunction<List<Integer>> {

    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessType()
     */
    public FitnessType fitnessType() {
        return FitnessType.MAXIMUM_BEST;
    }

    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessValue(com.erigir.lamark.Individual)
     */
    public double fitnessValue(Individual div) {
        List vals = (List) div.getGenome();

        double score = 0;
        for (int i = 0; i < vals.size(); i++) {
            Integer next = (Integer) vals.get(i);
            score += (i * next);
        }
        return score;
    }

}