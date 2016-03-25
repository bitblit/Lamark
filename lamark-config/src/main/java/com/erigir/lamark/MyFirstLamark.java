package com.erigir.lamark;

import com.erigir.lamark.creator.StringCreator;
import com.erigir.lamark.crossover.StringSinglePointCrossover;
import com.erigir.lamark.events.*;
import com.erigir.lamark.fitness.StringFinderFitness;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.RouletteWheelSelector;

import java.util.Arrays;
import java.util.TreeSet;

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
public class MyFirstLamark implements LamarkEventListener {
    /**
     * Bootstrap main to run from command line.
     *
     * @param ignored String[]
     */
    public static void main(String[] ignored) {
        MyFirstLamark e = new MyFirstLamark();
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
        StreamLamark<String> lamark = new StreamLamark.LamarkBuilder<String>()
                .withCreator(StringCreator.alphaCreator(6))
                .withCrossover(new StringSinglePointCrossover())
                .withFitnessFunction(new StringFinderFitness("LAMARK"))
                .withMutator(new StringSimpleMutator())
                .withSelector(new RouletteWheelSelector<String>())
                .withPopulationSize(50)
                .withPMutation(.01)
                .withPCrossover(1.0)
                .withUpperElitism(.1)
                .withLowerElitism(.1)
                .withTargetScore(1.0)
                .build();

        // Setup self as a listener
        lamark.addListener(this, new TreeSet<>(Arrays.asList(BetterIndividualFoundEvent.class, PopulationCompleteEvent.class, ExceptionEvent.class, LastPopulationCompleteEvent.class)));

        lamark.start();
    }


    /**
     * Implementation of listener function for lamark.
     * In this simple case, we just output events recieved to the
     * command line (standard out).
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        System.out.println("Received event: " + je);

        if (je instanceof ExceptionEvent) {
            ((ExceptionEvent) je).getException().printStackTrace();
        }

        if (je instanceof LastPopulationCompleteEvent) {
            System.out.println("Finished, best found was: " + je.getLamark().getBestSoFar());
            System.out.println("Total time spent was: " + je.getLamark().getRunTime() + " ms");
        }

    }

}
