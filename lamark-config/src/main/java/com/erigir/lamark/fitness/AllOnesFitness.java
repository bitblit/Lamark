package com.erigir.lamark.fitness;

import java.util.function.ToDoubleFunction;

/**
 * A simple fitness function to find a string with all ones in it.
 * <p/>
 * This class is here mainly as a simple example and because it's used in the unit tests of the roulette wheel
 *
 * @author cweiss
 * @since 04/2006
 */
public class AllOnesFitness implements ToDoubleFunction<String> {

    @Override
    public double applyAsDouble(String s) {
        double rval = 0;

        for (char c:s.toCharArray())
        {
            rval+=(c=='1')?1:0;
        }
        return rval / (double) s.length();
    }
}


