package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.Population;
import com.erigir.lamark.Individual;
import com.erigir.lamark.StreamLamark;

import java.util.List;

/**
 * This event is fired when the last population has been generated, which
 * is to say that Lamark is about to terminate.  The calling program can
 * hang onto the data by simply maintaining a reference to the
 * Lamark object held in this event. NOTE: If an object is registered
 * to receive the PopulationComplete event and this event, it
 * will receive BOTH, not just this one.
 *
 * @author cweiss
 * @since 03/2005
 */
public class LastPopulationCompleteEvent<T> extends PopulationCompleteEvent<T> {

    /**
     * Type of last population it was : ie, the reason the instance stopped *
     */
    private Type type;

    /**
     * Default constructor
     *
     * @param pLamark    Lamark object that generated the exception
     * @param pPop       Population that was last
     * @param finishType Type of the finishing event
     */
    public LastPopulationCompleteEvent(StreamLamark pLamark, List<Individual<T>> pPop, Long generationNumber, Type finishType) {
        super(pLamark, pPop, generationNumber);
        type = finishType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Completed last population " + type + ", #" + getGenerationNumber() + " pop=" + getLamark().format(getPopulation());
    }

    /**
     * Enumeration of the ways a lamark instance can terminate.
     *
     * @author cweiss
     * @since 11/2007
     */
    public enum Type {
        /**
         * GA ended because the maximum population number was reached *
         */
        BY_POPULATION_NUMBER,
        /**
         * GA ended because the target score was reached *
         */
        BY_TARGET_SCORE,
        /**
         * GA was aborted *
         */
        ABORTED,
        /**
         * GA ended because all individuals were the same *
         */
        UNIFORM
    }


}
