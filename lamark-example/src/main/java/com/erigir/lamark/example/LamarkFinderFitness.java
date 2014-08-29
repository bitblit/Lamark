package com.erigir.lamark.example;

import com.erigir.lamark.builtin.StringFinderFitness;

/**
 * A fitness function that tries to find the word 'LAMARK'
 * <p/>
 * Relies on the StringFinderFitness class to implement the fitness
 * function.
 *
 * @author cweiss
 * @since 11/2007
 */
public class LamarkFinderFitness extends StringFinderFitness {

    /**
     * Default Constructor
     */
    public LamarkFinderFitness() {
        super("LAMARK");
    }
}
