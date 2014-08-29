package com.erigir.lamark;

import com.erigir.lamark.annotation.*;

import java.util.List;

/**
 * Created by chrweiss on 7/22/14.
 */
@LamarkConfiguration
public class DynamicLamarkConfig<T> {

    /**
     * Determines whether to track the parents of each individual.  Use with care, as turning this on will consume large amounts of memory in a short
     * period of time - defaults to false
     */
    private boolean trackParentage = false;
    /**
     * Determines whether the algorithm should stop if it ever reaches the state of all the individuals being equals (using .equals function)  Defaults to true *
     */
    private boolean abortOnUniformPopulation = true;
    /**
     * Seed to use for the random number generator.  Defaults to system current time.  Set to a constant to generate reproducible runs.  NOTE: runs
     * are only reproducible if this is a constant AND the number of worker threads is 1, since otherwise the order of processing is dependant on
     * OS scheduling *
     */
    private Long randomSeed;

    @Creator
    public T create() {
        return null;
    }

    @Crossover
    public List<T> crossover(List<T> source, int size) {
        return null;
    }

    @FitnessFunction
    public double fitness(T source) {
        return 0;
    }

    @IndividualFormatter
    public String format(T input) {
        return "";
    }

    @Mutator
    public T mutate(T input) {
        return null;
    }

    @Param("maximumPopulations")
    public Integer getMaximumPopulations() {
        return null;
    }

    @Param("populationSize")
    public Integer getPopulationSize() {
        return null;
    }

    @Param("upperElitism")
    public double getUpperElitism() {
        return 0.0;
    }

    @Param("lowerElitism")
    public double getLowerElitism() {
        return 0.0;
    }

    @Param("crossoverProbability")
    public double getCrossoverProbability() {
        return 0.0;
    }

    @Param("mutationProbability")
    public double getMutationProbability() {
        return 0.0;
    }

    @Param("numberOfWorkerThreads")
    public Integer getNumberOfWorkerThreads() {
        return null;
    }

    @Param("targetScore")
    public Double getTargetScore() {
        return null;
    }

}
