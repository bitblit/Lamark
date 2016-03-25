package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.stream.StreamLamark;

/**
 * An event that is fired if SingleThreadedLamark is "aborted" for some
 * reason.
 *
 * @author cweiss
 * @since 03/2005
 */
public class AbortedEvent extends LamarkEvent {

    /**
     * Default constructor
     *
     * @param pLamark Lamark object that generated the exception
     */
    public AbortedEvent(StreamLamark pLamark) {
        super(pLamark);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AbortedEvent";
    }
}
