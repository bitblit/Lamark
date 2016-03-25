package com.erigir.lamark.events;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;

import java.util.List;

/**
 * An event that is fired ever time Lamark completes processing
 * another generation, or "Population".
 *
 * @author cweiss
 * @since 03/2005
 */
public class PopulationCompleteEvent<T> extends LamarkEvent {

    /**
     * Population that was just completed *
     */
    private List<Individual<T>> population;
    private Long generationNumber;

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     * @param pPop    Population that was just completed
     */
    public PopulationCompleteEvent(Lamark pLamark, List<Individual<T>> pPop, Long generationNumber) {
        super(pLamark);
        this.population = pPop;
        this.generationNumber = generationNumber;
    }

    /**
     * Accessor method
     *
     * @return Population that was completed
     */
    public List<Individual<T>> getPopulation() {
        return population;
    }

    /**
     * Accessor method
     *
     * @return Long containing the number of the generation
     */
    public Long getGenerationNumber() {
        return generationNumber;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Completed population #" + generationNumber + " best score : " + population.get(0).getFitness() + " pop=" + getLamark().format(population);
    }

}
