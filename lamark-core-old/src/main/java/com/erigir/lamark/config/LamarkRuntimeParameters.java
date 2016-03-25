package com.erigir.lamark.config;

/**
 * A data object holding the various runtime parameters of a lamark instance but no plugin defintions
 *
 * @see LamarkConfig
 * @see com.erigir.lamark.LamarkFactory
 * <p/>
 * <ul>
 * <li>maximumPopulations - Number of populations to run before stopping (note - if targetScore is set, lamark may stop earlier)</li>
 * <li>populationSize - Number of individuals in a population</li>
 * <li>upperElitism - Percent of the population to reserve across populations (ie, if set to .1 in a 20-individual population, then
 * the best 2 individuals will be copied to the next populations each cycle</li>
 * <li>lowerElitism - Percent of the population to discard each generation (ie, if set to .1 in a 20-individual population, then 2
 * individuals will be generated from scratch each population.  NOTE - this essentially functions as a massive mutation program.  Use
 * with care to avoid simply performing a random walk of the search space</li>
 * <li>crossoverProbability - Likelyhood that a crossover will occur (instead of a copy of one of the parents).  If set to 1, a crossover
 * always occurs</li>
 * <li>mutationProbability - Likelyhood that a mutation will occur <em>in a given individual</em>.  If set to 0, a mutation never occurs</li>
 * <li>individualSize - Used by many creators to determine how large a genome to make.</li>
 * <li>numberOfWorkerThreads - Number of threads to use in processing (not counting the main lamark thread)</li>
 * <li>targetScore - A score that, if reached, should cause Lamark to stop.  Lamark will stop if the current best score is >= this in the
 * case of a maxima search, or <= this in the case of a minima search</li>
 * <li>randomSeed - Value to use as the seed of the random number generator.  Used to get reproduceable runs of the GA - as long as the
 * number of worker threads is set to 1, and the runs are on machines with the same implementation of the JVM</li>
 * </ul>
 * <p/>
 * User: cweiss
 * Date: 2/15/14
 * Time: 1:28 PM
 */
public class LamarkRuntimeParameters {

    /**
     * Maximum number of generations to run.  If null, won't terminate due to generation number *
     */
    private Integer maximumPopulations = null;

    /**
     * Number of individuals to have in a given generation.  REQUIRED PROPERTY *
     */
    private Integer populationSize = 100;

    /**
     * Percentage of individuals (0.0 - 1.0) to retain each generation as upper elitism.  NOTE: this number is multiplied by populationSize and the result is truncated to an integer *
     */
    private Double upperElitism = 0.0;

    /**
     * Percentage of individuals (0.0 - 1.0) to replace each generation as lower elitism.  NOTE: this number is multiplied by populationSize and the result is truncated to an integer *
     */
    private Double lowerElitism = 0.0;

    /**
     * Likelihood (0.0 - 1.0) of an individual being generated via crossover.  If the check fails, the first parent is copied instead. *
     */
    private Double crossoverProbability = 1.0;

    /**
     * Likelihood (0.0 - 1.0) of an individual being mutated.  This is on an individual by individual basis, not a population-wide basis.  Individuals retained via upper elitism are
     * immune to mutation. *
     */
    private Double mutationProbability = 0.005;

    /**
     * Number of threads to use to process work packages.  Defaults to 1. *
     */
    private Integer numberOfWorkerThreads = 1;

    /**
     * A score which, if reached, will cause the algorithm to terminate.  If null, the algorithm will never terminate due to score *
     */
    private Double targetScore;

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


    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getMaximumPopulations() {
        return maximumPopulations;
    }

    /**
     * Mutator method
     *
     * @param maximumPopulations new value for the property
     */
    public void setMaximumPopulations(Integer maximumPopulations) {
        this.maximumPopulations = maximumPopulations;
    }

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getPopulationSize() {
        return populationSize;
    }

    /**
     * Mutator method
     *
     * @param populationSize new value for the property
     */
    public void setPopulationSize(Integer populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * Accessor method
     *
     * @return Double containing the property
     */
    public Double getUpperElitism() {
        return upperElitism;
    }

    /**
     * Mutator method
     *
     * @param upperElitism new value for the property
     */
    public void setUpperElitism(Double upperElitism) {
        this.upperElitism = upperElitism;
    }

    /**
     * Accessor method
     *
     * @return Double containing the property
     */
    public Double getLowerElitism() {
        return lowerElitism;
    }

    /**
     * Mutator method
     *
     * @param lowerElitism new value for the property
     */
    public void setLowerElitism(Double lowerElitism) {
        this.lowerElitism = lowerElitism;
    }

    /**
     * Accessor method
     *
     * @return Double containing the property
     */
    public Double getCrossoverProbability() {
        return crossoverProbability;
    }

    /**
     * Mutator method
     *
     * @param crossoverProbability new value for the property
     */
    public void setCrossoverProbability(Double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    /**
     * Accessor method
     *
     * @return Double containing the property
     */
    public Double getMutationProbability() {
        return mutationProbability;
    }

    /**
     * Mutator method
     *
     * @param mutationProbability new value for the property
     */
    public void setMutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getNumberOfWorkerThreads() {
        return numberOfWorkerThreads;
    }

    /**
     * Mutator method
     *
     * @param numberOfWorkerThreads new value for the property
     */
    public void setNumberOfWorkerThreads(Integer numberOfWorkerThreads) {
        this.numberOfWorkerThreads = numberOfWorkerThreads;
    }

    /**
     * Accessor method
     *
     * @return Double containing the property
     */
    public Double getTargetScore() {
        return targetScore;
    }

    /**
     * Mutator method
     *
     * @param targetScore new value for the property
     */
    public void setTargetScore(Double targetScore) {
        this.targetScore = targetScore;
    }

    /**
     * Accessor method
     *
     * @return boolean containing the property
     */
    public boolean isTrackParentage() {
        return trackParentage;
    }

    /**
     * Mutator method
     *
     * @param trackParentage new value for the property
     */
    public void setTrackParentage(boolean trackParentage) {
        this.trackParentage = trackParentage;
    }

    /**
     * Accessor method
     *
     * @return boolean containing the property
     */
    public boolean isAbortOnUniformPopulation() {
        return abortOnUniformPopulation;
    }

    /**
     * Mutator method
     *
     * @param abortOnUniformPopulation new value for the property
     */
    public void setAbortOnUniformPopulation(boolean abortOnUniformPopulation) {
        this.abortOnUniformPopulation = abortOnUniformPopulation;
    }

    /**
     * Accessor method
     *
     * @return Long containing the property
     */
    public Long getRandomSeed() {
        return randomSeed;
    }

    /**
     * Mutator method
     *
     * @param randomSeed new value for the property
     */
    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
    }


}


