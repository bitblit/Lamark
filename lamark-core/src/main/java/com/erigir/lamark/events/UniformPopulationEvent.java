package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.Population;

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
 *
 */
public class UniformPopulationEvent extends PopulationCompleteEvent {

    /**
     * Default constructor
     * @param pLamark Lamark object that generated the exception
     * @param pPop Population object that was uniform
     */
	public UniformPopulationEvent(Lamark pLamark,Population pPop)
	{
		super(pLamark,pPop);
	}
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Population Uniform.  Population #"+getPopulation().getNumber();
    }
}
