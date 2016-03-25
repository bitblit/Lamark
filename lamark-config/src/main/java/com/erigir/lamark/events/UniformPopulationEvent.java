package com.erigir.lamark.events;

import com.erigir.lamark.Individual;
import com.erigir.lamark.StreamLamark;

import java.util.List;

/**
 * This event is fired when all the members of a given population are
 * "equal", that is, that they have the same genome, even if
 * they may have different parents.  Please note that
 * if an object is registered for both this event and the
 * PopulationCompleteEvent, it will receive notifications
 * for BOTH, not just this one.
 *
 * @author cweiss
 * @since 11/2006
 */
public class UniformPopulationEvent<T> extends PopulationCompleteEvent {

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     * @param currentGeneration    Population that was uniform
     * @param generationNumber Long containing the idx of the current generation
     */
    public UniformPopulationEvent(StreamLamark pLamark, List<Individual<T>> currentGeneration, Long generationNumber) {
        super(pLamark, currentGeneration, generationNumber);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Population Uniform.  Population #" + getGenerationNumber();
    }
}
