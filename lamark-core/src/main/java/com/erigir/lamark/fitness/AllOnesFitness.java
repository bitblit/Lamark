package com.erigir.lamark.fitness;

import java.util.function.ToDoubleFunction;

/**
 * A simple fitness function to find a string with all ones in it.
 * &lt;p /&gt;
 * This class is here mainly as a simple example and because it's used in the unit tests of the roulette wheel
 * &lt;p /&gt;
 * Can be used to test 'minimize' score settings (which should result in all zeros)
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


