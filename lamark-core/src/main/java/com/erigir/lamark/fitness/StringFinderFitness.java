package com.erigir.lamark.fitness;

import com.erigir.lamark.*;
import com.erigir.lamark.annotation.FitnessFunction;
import com.erigir.lamark.annotation.Param;

/**
 * A fitness function that searches for a given string.
 * <p/>
 * The fitness function assumes that a string is passed, and
 * its score is the ratio of correct letters to all letters.
 * When this ratio is 1.0, the target string has been found.
 *
 * @author cweiss
 * @since 10/2007
 */
public class StringFinderFitness {
    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessValue(Individual)
     */
    @FitnessFunction
            (
                    fitnessType =  EFitnessType.MAXIMUM_BEST,
                    description = "Calculates a value that increases as the string approaches the target string"
            )
    public double fitnessValue(@Param("target")String target, String s) {
        double rval = 0;
        if (s.length() == target.length()) {
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == target.charAt(j)) {
                    rval++;
                }
            }
        }
        return rval / (double) s.length();
    }


}


