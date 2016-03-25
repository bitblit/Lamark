package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.StreamLamark;

/**
 * This event is thrown whenever an exception occurs within the code
 * being run by the Lamark instance.
 *
 * @author cweiss
 * @since 03/2005
 */
public class ExceptionEvent extends LamarkEvent {
    /**
     * Wrapped exception object *
     */
    private Throwable exception;

    /**
     * Default constructor
     *
     * @param pLamark    Lamark object that generated the exception
     * @param pException Exception object that occurred
     */
    public ExceptionEvent(StreamLamark pLamark, Throwable pException) {
        super(pLamark);
        exception = pException;
    }

    /**
     * Get handle to the wrapped exception
     *
     * @return Throwable containing the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ExceptionEvent:" + exception.toString();
    }
}
