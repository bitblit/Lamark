package com.erigir.lamark.builtin;

import com.erigir.lamark.WorkPackage;
import com.erigir.lamark.annotation.*;
import com.erigir.lamark.config.ERuntimeParameters;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A simple configuration that searches for the word LAMARK using a GA
 *
 * @author cweiss
 * @since 11/2007
 */
@LamarkConfiguration
public class MyFirstLamarkConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MyFirstLamarkConfig.class);

    private StringFinderFitness fitness = new StringFinderFitness();
    private StringCrossover crossover = new StringCrossover();
    private StringMutator mutator = new StringMutator();
    private StringCreator creator = new StringCreator();

    @FitnessFunction
    public double calculateFitness(@Param("target") String target, String input) {
        return fitness.fitnessValue(target, input);
    }

    @Creator
    public String createIndividual(@Param("target") String target, @Param("random") Random random) {
        return creator.createAlphaString(target.length(), random);
    }

    @Crossover
    public String crossover(@Parent String p1, @Parent String p2, @Param("random") Random random) {
        return crossover.crossoverString(p1, p2, random);
    }

    @Mutator
    public String mutate(String input, @Param("random") Random random) {
        return mutator.singlePointMutate(input, random);
    }

    @IndividualFormatter
    public String format(String input) {
        return input;
    }

    @PreloadIndividuals
    public List<String> preloadedIndividuals() {
        return Arrays.asList("AAAAAA");
    }

    @Param("target")
    public String getTarget() {
        return "LAMARK";
    }

    @Param("")
    public Map<String, Object> getMultiParams() {
        Map<String, Object> multi = new TreeMap<>();
        multi.put(ERuntimeParameters.TARGET_SCORE.getPropertyName(), 1.0);
        multi.put(ERuntimeParameters.MUTATION_PROBABILITY.getPropertyName(), .01);
        multi.put(ERuntimeParameters.LOWER_ELITISM.getPropertyName(), 0.0);
        multi.put(ERuntimeParameters.UPPER_ELITISM.getPropertyName(), 0.0);
        multi.put(ERuntimeParameters.NUMBER_OF_WORKER_THREADS.getPropertyName(), 400);
        multi.put(ERuntimeParameters.POPULATION_SIZE.getPropertyName(), 50);

        return multi;
    }

    /**
     * Implementation of listener function for lamark.
     * In this simple case, we just output events recieved to the
     * standard logger
     *
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
