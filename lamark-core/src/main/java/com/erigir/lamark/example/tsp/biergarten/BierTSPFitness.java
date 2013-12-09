/*
 * Created on Apr 1, 2005
 */
package com.erigir.lamark.example.tsp.biergarten;

import com.erigir.lamark.example.tsp.TSPFitness;


/**
 * Thin wrapper around the TSPFitness class, forcing the Biergarten TSP.
 * 
 * @author cweiss
 * @since 04/2005
 */
public class BierTSPFitness extends TSPFitness
{
    /** Define the best solution **/
	public static int BEST_SOLUTION=118282;
	
    /**
     * Default constructor. 
     */
    public BierTSPFitness()
    {
        super();
        this.setTspFile("bier127.tsp");
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