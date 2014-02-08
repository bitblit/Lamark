/*
 * Created on Apr 1, 2005
 */
package com.erigir.lamark.example.tsp.ulysses;

import com.erigir.lamark.example.tsp.TSPFitness;


/**
 * Thin wrapper around the TSPFitness class, forcing the Ulysses TSP.
 *
 * @author cweiss
 * @since 04/2005
 */
public class UlyssesTSPFitness extends TSPFitness {
    /**
     * Define the best solution *
     */
    public static int BEST_SOLUTION = 6859;

    /**
     * Default constructor.
     */
    public UlyssesTSPFitness() {
        super();
        this.setTspFile("ulysses16.tsp");
        this.setBestKnown(BEST_SOLUTION);
    }

    /**
     * @see com.erigir.lamark.example.tsp.TSPFitness#getBestKnown()
     */
    public Integer getBestKnown() {
        return BEST_SOLUTION;
    }


}