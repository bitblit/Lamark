package com.erigir.lamark.events;

import com.erigir.lamark.Lamark;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This event is fired whenever a logging event occurs in Lamark.
 * <p>
 * NOTE: this type of event is meant to allow bridging Lamark logging
 * into the logger of whatever container is being used.  To simplify
 * bridging, an adapter for standard java logging is provided (either
 * call handleWithJavaLogger, or use the JavaLoggingAdapter class.
 *
 * @author cweiss
 * @see com.erigir.lamark.JavaLoggingAdapter
 * @since 11/2007
 */
public class LogEvent extends LamarkEvent {
    /**
     * Message to be logged *
     */
    private Object message;
    /**
     * java.util.logging.Level of the message *
     */
    private Level level;

    /**
     * Default constructor
     *
     * @param lamark  Lamark object that generated the exception
     * @param message Object to log as a message
     * @param lvl     Level to log the message at
     */
    public LogEvent(Lamark lamark, Object message, Level lvl) {
        super(lamark);
        this.message = message;
        this.level = lvl;
    }

    /**
     * Adapter method to handle a LogEvent with a java.util.logging.Logger object
     *
     * @param evt    LogEvent to handle
     * @param logger Logger to log the event
     */
    public static void handleWithJavaLogger(LogEvent evt, Logger logger) {
        if (evt.message != null) {
            logger.log(evt.level, evt.message.toString());
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LogEvent[lamark=" + this.getLamark() + ",level=" + level + ",message=" + message + "]";
    }

    /**
     * Accessor method.
     *
     * @return Level containing the property
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Mutator method
     *
     * @param level new value
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Accessor method
     *
     * @return Object containig the property
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Mutator Method
     *
     * @param message new value
     */
    public void setMessage(Object message) {
        this.message = message;
    }

}
