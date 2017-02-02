package com.erigir.lamark.example;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkBuilder;
import com.erigir.lamark.crossover.StringSinglePoint;
import com.erigir.lamark.events.*;
import com.erigir.lamark.fitness.StringFinderFitness;
import com.erigir.lamark.listener.StandardOutLoggingListener;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.TournamentSelector;
import com.erigir.lamark.supplier.AlphaStringSupplier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.*;

/**
 * A simple command-line program that searches for the word LAMARK using a GA.
 *
 * This class demonstrates how to simply wire a java command line client
 * to run Lamark internally.  Typically used as a stub for further work.
 * For serious work, I'd recommend the use of an inversion of control
 * container (such as Spring) rather than coding a lamark instance directly
 * like this.
 *
 * @author cweiss
 * @since 11/2007
 */
public class MyFirstLamark{
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
     */
    public void go() {

        Lamark<String> lamark = createBuilder().build();
        // Setup self as a listener
        lamark.addListener(new StandardOutLoggingListener(), new HashSet<>(Arrays.asList(BetterIndividualFoundEvent.class, PopulationCompleteEvent.class, ExceptionEvent.class, LastPopulationCompleteEvent.class)));
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("About to start lamark, will be allowed to run for 5 minutes max");
        Future<String> future = executor.submit(lamark);
        // Could run in the main thread by calling lamark.start() instead
        // But then any timeouts have to be manually written
        // If a "final" result is found (e.g., homogenous population) then this will return early.
        try {
            String result = future.get(5, TimeUnit.MINUTES);
        }
        catch (InterruptedException | ExecutionException ee)
        {
            System.out.println("Ran into an exception");
            ee.printStackTrace();
        }
        catch (TimeoutException te)
        {
            Individual<String> best = lamark.getBestSoFar();
            System.out.println("Timed out - best so far was "+best);
        }

    }

    public LamarkBuilder<String> createBuilder()
    {
        return new LamarkBuilder<String>()
                .withSupplier(new AlphaStringSupplier(6))
                .withCrossover(new StringSinglePoint())
                .withFitnessFunction(new StringFinderFitness("LAMARK"))
                .withMutator(new StringSimpleMutator())
                .withSelector(new TournamentSelector<>())
                .withPopulationSize(50)
                .withMutationProbability(.01)
                .withCrossoverProbability(1.0)
                .withUpperElitism(.1)
                .withLowerElitism(.1)
                .withTargetScore(1.0);
    }

}
