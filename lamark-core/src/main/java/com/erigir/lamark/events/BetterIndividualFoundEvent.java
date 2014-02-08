package com.erigir.lamark.events;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.Population;

/**
 * LamarkEvent object sent every time a new better individual is found.
 *
 * @author cweiss
 * @since 03/2005
 */
public class BetterIndividualFoundEvent extends PopulationCompleteEvent {

    /**
     * Handle to the new better individual
     */
    private Individual newBest;

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     * @param pPop    Population object containing the new best individual
     * @param better  Individual object that is the new top
     */
    public BetterIndividualFoundEvent(Lamark pLamark, Population pPop, Individual better) {
        super(pLamark, pPop);
        this.newBest = better;
    }


    /**
     * Get a handle to the new best individual referred to
     *
     * @return Individual object that is the new best
     */
    public Individual getNewBest() {
        return newBest;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "BetterIndividualFound : Pop # " + getPopulation().getNumber() + " Score : " + newBest.getFitness() + " Value=" + getLamark().format(newBest);
    }
}
