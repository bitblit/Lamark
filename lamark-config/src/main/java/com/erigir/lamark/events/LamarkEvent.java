package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.stream.StreamLamark;

/**
 * The base class for all Lamark-created events.  All lamark events
 * hold a reference to the lamark object, so that listeners can
 * perform top-level actions like process abort.
 *
 * @author cweiss
 * @since 03/2005
 */
public abstract class LamarkEvent {
    /**
     * Handle to the generating lamark instance
     */
    private StreamLamark lamark;

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     */
    public LamarkEvent(StreamLamark pLamark) {
        lamark = pLamark;
    }

    /**
     * Get a handle to the generating lamark instance
     *
     * @return Lamark instance that created the event
     */
    public StreamLamark getLamark() {
        return lamark;
    }
}
