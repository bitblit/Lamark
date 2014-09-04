package com.erigir.lamark;

import com.erigir.lamark.annotation.*;
import com.erigir.lamark.builtin.MyFirstLamarkConfig;
import com.erigir.lamark.config.ERuntimeParameters;
import com.erigir.lamark.config.IntrospectLamarkFactory;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A simple command-line program that searches for the word LAMARK using a GA.
 * <p/>
 * This class demonstrates how to simply wire a java command line client
 * to run Lamark internally.  Typically used as a stub for further work.
 * For serious work, I'd recommend the use of an inversion of control
 * container (such as Spring) rather than coding a lamark instance directly
 * like this.
 *
 * @author cweiss
 * @since 11/2007
 */
@LamarkConfiguration
public class MyFirstLamarkCLI {
    private static final Logger LOG = LoggerFactory.getLogger(MyFirstLamarkCLI.class);

    /**
     * Bootstrap main to run from command line.
     *
     * @param ignored String[]
     */
    public static void main(String[] ignored) {
        MyFirstLamarkCLI e = new MyFirstLamarkCLI();
        e.go();
        System.exit(0);
    }

    /**
     * Creates an instance of lamark, configures it, and then runs it.
     * <p/>
     * NOTE: In this case, we are running lamark within the same thread as
     * the CLI itself (although Lamark may start other threads as well, depending
     * on the value of "numberOfWorkerThreads").  Typically, (especially in GUI
     * apps) Lamark should be run in it's own thread, and monitored by listening for
     * "last population" events.  This can be done by calling:
     * new Thread(lamark).start();
     * Since lamark implements Runnable.
     */
    public void go() {
        IntrospectLamarkFactory ilf = new IntrospectLamarkFactory(new MyFirstLamarkConfig());
        // lamark will introspect this object and point to all the correct functions
        Lamark lamark = ilf.createConfiguredLamarkInstance();
        lamark.call();
    }

}
