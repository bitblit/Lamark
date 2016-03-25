package com.erigir.lamark.fitness;

import java.util.function.ToDoubleFunction;

/**
 * A fitness function that searches for a given string.
 * &lt;p /&gt;
 * The fitness function assumes that a string is passed, and
 * its score is the ratio of correct letters to all letters.
 * When this ratio is 1.0, the target string has been found.
 *
 * @author cweiss
 * @since 10/2007
 */
public class StringFinderFitness implements ToDoubleFunction<String> {
    /**
     * The target string for lamark to search for *
     */
    private String target;

    /**
     * Constructor that also defines the target string
     *
     * @param pTarget String to search for
     */
    public StringFinderFitness(String pTarget) {
        super();
        target = pTarget;
    }

    @Override
    public double applyAsDouble(String s) {
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


