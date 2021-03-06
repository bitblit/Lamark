/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import java.text.DecimalFormat;

/**
 * A set of simple static functions used by Lamark.
 *
 * @author cweiss
 * @since 04/2006
 */
public class LamarkUtil {
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
    private static LamarkUtil instance = new LamarkUtil();
    /**
     * Formatter instance *
     */
    private DecimalFormat formatter;

    /**
     * Private constructor to enforce singleton.
     */
    private LamarkUtil() {
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
    public static String formatISO(Long milliSecs) {
        StringBuffer buf = new StringBuffer(32);

        if (milliSecs!=null) {
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
        }
        return buf.toString();
    }

    /**
     * Builds a string describing the current version.
     *
     * @return String containing the version number
     */
    public static String getVersion() {
        return LamarkUtil.class.getPackage().getImplementationVersion();
    }

    /**
     * Format a double into a string with at most 2 decimal places.
     *
     * @param d double to format
     * @return String contianing the formatted double
     */
    public static String format(double d) {
        return LamarkUtil.instance.formatter.format(d);
    }



}
