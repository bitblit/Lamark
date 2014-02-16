package com.erigir.lamark.example;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.WorkPackage;
import com.erigir.lamark.config.LamarkRuntimeParameters;
import com.erigir.lamark.crossover.StringSinglePoint;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.RouletteWheel;

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
        Lamark lamark = new Lamark();
        LamarkFinderFitness fitness = new LamarkFinderFitness();
        lamark.setFitnessFunction(fitness);
        lamark.setCrossover(new StringSinglePoint());
        lamark.setSelector(new RouletteWheel());
        lamark.setMutator(new StringSimpleMutator());
        lamark.setCreator(new LamarkFinderCreator());

        LamarkRuntimeParameters lrp = new LamarkRuntimeParameters();


        lrp.setNumberOfWorkerThreads(400);
        lrp.setPopulationSize(50);

        lrp.setTargetScore(1.0);
        lrp.setMutationProbability(.01);
        lrp.setLowerElitism(.1);
        lrp.setUpperElitism(.1);

        lamark.setRuntimeParameters(lrp);


        // Setup self as a listener
        lamark.addBetterIndividualFoundListener(this);
        lamark.addPopulationCompleteListener(this);
        lamark.addExceptionListener(this);
        lamark.addLastPopulationCompleteListener(this);
        lamark.addConfigurationListener(this);


        lamark.run();
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
            System.out.println("Finished, best found was: " + je.getLamark().getCurrentBest());
            System.out.println("WP Queue size grew to: " + WorkPackage.queueSize() + " for a population size of " + je.getLamark().getRuntimeParameters().getPopulationSize());
            System.out.println("Total time spent was: " + je.getLamark().getTotalRunTime() + " ms");
            System.out.println("Total wait time was: " + je.getLamark().getTotalWaitTime() + " ms");
            System.out.println("Average wait time was: " + je.getLamark().getAverageWaitTime() + " ms");
            int pct = (int) (100.0 * je.getLamark().getPercentageTimeWaiting());
            System.out.println("Wait time was: " + pct + "% of the total time");
        }

    }

}
