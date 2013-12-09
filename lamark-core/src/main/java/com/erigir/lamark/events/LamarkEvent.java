package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;

/**
 * The base class for all Lamark-created events.  All lamark events
 * hold a reference to the lamark object, so that listeners can
 * perform top-level actions like process abort.
 * 
 * @author cweiss
 * @since 03/2005
 */
public abstract class LamarkEvent {
	/** Handle to the generating lamark instance */
    private Lamark lamark;
    
    /**
     * Default constructor
     * @param pLamark Lamark object that generated the exception
     */
	public LamarkEvent(Lamark pLamark)
	{
        lamark=pLamark;
	}
	
    /**
     * Get a handle to the generating lamark instance
     * @return Lamark instance that created the event
     */
	public Lamark getLamark() {
		return lamark;
	}
}
