/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * A set of simple static functions used by Lamark.
 *
 * @author cweiss
 * @since 04/2006
 */
public class Util {
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);
    /**
     * Static constant *
     */
    private static final int MILLIS_IN_SECOND = 1000;
    /**
     * Static constant *
     */
    private static final int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
    /**
     * Static constant *
     */
    private static final int MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    /**
     * Static instance to hold cache values *
     */
    private static Util instance = new Util();
    /**
     * Formatter instance *
     */
    private DecimalFormat formatter;

    /**
     * Private constructor to enforce singleton.
     */
    private Util() {
        super();
        formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(2);
    }

    /**
     * A method that will take a milliseconds number and convert it to a String
     * describing hours, minutes, and seconds. It's designed for displaying time
     * intervals, not absolute times.
     *
     * @param milliSecs The time interval to convert to a string.
     * @return A string representing the time interval.
     */
    public static String formatISO(long milliSecs) {
        int hours, minutes, seconds, milliseconds;
        boolean isneg = false; // for handling negative times.
        if (milliSecs < 0) {
            isneg = true;
            milliSecs = -milliSecs;
        }
        hours = (int) (milliSecs / MILLIS_IN_HOUR);
        milliSecs %= MILLIS_IN_HOUR;
        minutes = (int) (milliSecs / MILLIS_IN_MINUTE);
        milliSecs %= MILLIS_IN_MINUTE;
        seconds = (int) (milliSecs / MILLIS_IN_SECOND);
        milliseconds = (int) (milliSecs % MILLIS_IN_SECOND);

        StringBuffer buf = new StringBuffer(32);
        if (isneg) {
            buf.append('-');
        }
        buf.append(hours).append(':').append((char) (minutes / 10 + '0'))
                .append((char) (minutes % 10 + '0')).append(':').append(
                (char) (seconds / 10 + '0'))
                .append((char) (seconds % 10 + '0')).append('.');
        // Make sure there are three digits in the milliseconds field.
        if (milliseconds < 10) {
            buf.append("00");
        } else if (milliseconds < 100) {
            buf.append('0');
        }
        buf.append(milliseconds);
        return buf.toString();
    }

    /**
     * This method is here because IMO these should have all been runtime exceptions
     * anyway...
     *
     * @param obj
     * @param m
     * @param args
     * @return
     */
    public static <T> T qExec(Class clazz, Object obj, Method m, Object... args) {
        try {
            return (T) m.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn("Error trying to invoke method {} on {} : {}", m, obj, args, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * This method is here because IMO these should have all been runtime exceptions
     * anyway...
     *
     * @param clazz
     * @return
     */
    public static <T> T qNewInstance(Class<T> clazz)
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Couldn't instantiate class"+clazz,e);
        }
    }

    /**
     * This method is here because IMO these should have all been runtime exceptions
     * anyway...
     *
     * @param name
     * @return
     */
    public static Class qForName(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Couldn't instantiate class"+name,e);
        }
    }


    /**
     * Builds a string describing the current version.
     *
     * @return String containing the version number
     */
    public static String getVersion() {
        return Util.class.getPackage().getImplementationVersion();
    }

    /**
     * Format a double into a string with at most 2 decimal places.
     *
     * @param d double to format
     * @return String contianing the formatted double
     */
    public static String format(double d) {
        return Util.instance.formatter.format(d);
    }

}
