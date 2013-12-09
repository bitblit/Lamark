/*
 * Created on Apr 1, 2005
 */
package com.erigir.lamark.example.tsp.germany;

import com.erigir.lamark.example.tsp.TSPFitness;


/**
 * Thin wrapper around the TSPFitness class, forcing the Germany TSP.
 * 
 * @author cweiss
 * @since 04/2005
 */
public class GermanyTSPFitness extends TSPFitness
{
    /** Define the best solution **/
	public static int BEST_SOLUTION=1573084;

    /**
     * Default constructor. 
     */
    public GermanyTSPFitness()
    {
        super();
        this.setTspFile("d15112.tsp");
        this.setBestKnown(BEST_SOLUTION);
    }

    /**
     * @see com.erigir.lamark.example.tsp.TSPFitness#getBestKnown()
     */
    public Integer getBestKnown()
    {
        return BEST_SOLUTION;
    }

}