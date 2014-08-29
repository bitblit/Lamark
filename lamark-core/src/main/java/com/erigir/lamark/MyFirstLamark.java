package com.erigir.lamark;

import com.erigir.lamark.annotation.*;
import com.erigir.lamark.config.ERuntimeParameters;
import com.erigir.lamark.config.LamarkRuntimeParameters;
import com.erigir.lamark.creator.GeneralStringCreator;
import com.erigir.lamark.creator.StringCreator;
import com.erigir.lamark.crossover.StringSinglePoint;
import com.erigir.lamark.crossover.StringSinglePoint2;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import com.erigir.lamark.fitness.StringFinderFitness;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.RouletteWheel;
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
public class MyFirstLamark {
    private static final Logger LOG = LoggerFactory.getLogger(MyFirstLamark.class);

    private StringFinderFitness fitness = new StringFinderFitness();
    private StringSinglePoint2 crossover = new StringSinglePoint2();
    private RouletteWheel selector = new RouletteWheel();
    private StringSimpleMutator mutator = new StringSimpleMutator();
    private GeneralStringCreator creator = new GeneralStringCreator();

    @FitnessFunction
    public double calculateFitness(@Param("target")String target, String input)
    {
        return fitness.fitnessValue(target,input);
    }

    @Creator
    public String createIndividual(@Param("target")String target,@Param("random")Random random)
    {
        return creator.createAlphaString(target.length(),random);
    }

    @Crossover
    public String crossover(@Parent String p1, @Parent String p2, @Param("random")Random random)
    {
        return crossover.crossoverString(p1,p2,random);
    }

    @Mutator
    public String mutate(String input, @Param("random")Random random)
    {
        return mutator.mutate(input,random);
    }

    @IndividualFormatter
    public String format(String input)
    {
        return input;
    }

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
        // lamark will introspect this object and point to all the correct functions
        Lamark lamark = new Lamark(this);
        lamark.call();
    }

    @PreloadIndividuals
    public List<String> preloadedIndividuals()
    {
        return Arrays.asList("AAAAAA");
    }

    @Param("workerThreadCount")
    public int getWorkerThreadCount()
    {
        return 400;
    }

    @Param("populationSize")
    public int getPopulationSize()
    {
        return 50;
    }

    @Param("targetScore")
    public Map<String,Object> getMultiParams()
    {
        Map<String,Object> multi = new TreeMap<>();
        multi.put("targetScore",1.0);
        multi.put("mutationProbability",.01);
        multi.put("lowerElitism",0.0);
        multi.put("upperElitism",0.0);
        multi.put("target","LAMARK");

        return multi;
    }

    /**
     * Implementation of listener function for lamark.
     * In this simple case, we just output events recieved to the
     * standard logger
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(LamarkEvent)
     */
    @LamarkEventListener
    public void handleEvent(LamarkEvent je) {
        LOG.info("Received event: " + je);

        if (je instanceof ExceptionEvent) {
            ((ExceptionEvent) je).getException().printStackTrace();
        }

        if (je instanceof LastPopulationCompleteEvent) {
            LOG.info("Finished, best found was: " + je.getLamark().getCurrentBest());
            LOG.info("WP Queue size grew to: " + WorkPackage.queueSize() + " for a population size of " + ERuntimeParameters.POPULATION_SIZE.read(je.getLamark().getRuntimeParameters()));
            LOG.info("Total time spent was: " + je.getLamark().getTotalRunTime() + " ms");
            LOG.info("Total wait time was: " + je.getLamark().getTotalWaitTime() + " ms");
            LOG.info("Average wait time was: " + je.getLamark().getAverageWaitTime() + " ms");
            int pct = (int) (100.0 * je.getLamark().getPercentageTimeWaiting());
            LOG.info("Wait time was: " + pct + "% of the total time");
        }

    }

}
