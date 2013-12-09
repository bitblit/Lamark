/*
 * Created on Apr 1, 2005
 */
package com.erigir.lamark.example.tsp.berlin;

import com.erigir.lamark.example.tsp.TSPFitness;

/**
 * Thin wrapper around the TSPFitness class, forcing the Berlin TSP.
 * 
 * @author cweiss
 * @since 04/2005
 */
public class BerlinTSPFitness extends TSPFitness
{
    /** Define the best solution **/
	public static int BEST_SOLUTION=7542;

    
    /**
     * Default constructor. 
     */
    public BerlinTSPFitness()
    {
        super();
        this.setTspFile("berlin52.tsp");
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