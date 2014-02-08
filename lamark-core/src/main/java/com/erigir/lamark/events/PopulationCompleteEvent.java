package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.Population;

/**
 * An event that is fired ever time Lamark completes processing
 * another generation, or "Population".
 *
 * @author cweiss
 * @since 03/2005
 */
public class PopulationCompleteEvent extends LamarkEvent {

    /**
     * Population that was just completed *
     */
    private Population population;

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     * @param pPop    Population that was just completed
     */
    public PopulationCompleteEvent(Lamark pLamark, Population pPop) {
        super(pLamark);
        this.population = pPop;
    }

    /**
     * Accessor method
     *
     * @return Population that was completed
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Completed population #" + population.getNumber() + " best score : " + population.best().getFitness() + " pop=" + getLamark().format(population.getIndividuals());
    }

}
