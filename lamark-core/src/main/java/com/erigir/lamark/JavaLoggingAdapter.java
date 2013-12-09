package com.erigir.lamark;

import java.util.logging.Logger;

import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.events.LogEvent;

/**
 * An adapter to forward Lamark logging messages to a java logger.
 * 
 * Since Lamark uses its event handlers for logging, and users of 
 * the system may want to use a standard java logger for logging,
 * this class provides a bridge by allowing a standard java logger
 * to be used as a LamarkEventListener.
 * 
 * @author cweiss
 * @since 9-17-07
 */
public class JavaLoggingAdapter implements LamarkEventListener
{
    /**
     * Java logger to forward the messages to.
     */
    public Logger logger;
    
    /**
     * Constructs a adapter wrapped around the passed logger.
     * @param logger Logger to adapt to Lamark
     */
    public JavaLoggingAdapter(Logger logger)
    {
        super();
        this.logger = logger;
    }

    /** 
     * Implementation of handleEvent for LamarkEventListener.
     * Passes any LogEvents on to the logger.
     * @param je LamarkEvent to pass to the logger
     */
    public void handleEvent(LamarkEvent je)
    {
        if (LogEvent.class.isInstance(je))
        {
            LogEvent.handleWithJavaLogger((LogEvent)je, logger);
        }
    }

}
