package com.erigir.lamark.fitness;

import com.erigir.lamark.EFitnessType;

/**
 * A simple fitness function to find a string with all ones in it.
 * <p/>
 * NOTE: This is here to simply demonstrate the use of a
 * minimum-best fitness function.  It just takes the AllOnes
 * class and inverts the function.
 * <p/>
 * This class is here mainly as a simple example and because it's used in the unit tests of the roulette wheel
 *
 * @author cweiss
 * @since 04/2006
 */

public class AllZeros extends AllOnes {
    /**
     * @see com.erigir.lamark.fitness.AllOnes#fitnessType()
     */
    public EFitnessType fitnessType() {
        return EFitnessType.MINIMUM_BEST;
    }
}
