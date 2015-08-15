package com.erigir.lamark.fitness;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.Individual;
import com.erigir.lamark.annotation.FitnessFunction;

/**
 * A simple fitness function to find a string with all ones in it.
 * <p/>
 * This class is here mainly as a simple example and because it's used in the unit tests of the roulette wheel
 *
 * @author cweiss
 * @since 04/2006
 */
public class AllOnes extends AbstractLamarkComponent implements IFitnessFunction<String> {

    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessValue(com.erigir.lamark.Individual)
     */
    public double fitnessValue(Individual div) {
        String test = (String) div.getGenome();
        double rval = 0;
        for (int i = 0; i < test.length(); i++) {
            if (test.charAt(i) == '1') {
                rval++;
            }
        }
        return rval;
    }

    @FitnessFunction(fitnessType = EFitnessType.MAXIMUM_BEST)
    public double allOnesFitness(String test)
    {
        double rval = 0;
        for (int i = 0; i < test.length(); i++) {
            if (test.charAt(i) == '1') {
                rval++;
            }
        }
        return rval;
    }

    public EFitnessType fitnessType() {
        return EFitnessType.MAXIMUM_BEST;
    }

}
