package com.erigir.lamark.config;

import com.erigir.lamark.ICreator;
import com.erigir.lamark.ICrossover;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.IMutator;
import com.erigir.lamark.events.LamarkEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class holding all the runtime parameters of a Lamark instance plus the classes necessary for the plugins.
 * <p/>
 * Given a fully filled out one of these objects, the
 * A data object holding all the configuration for a Lamark instance - for easy serialization of settings.
 * <p/>
 * This class does NOT hold any data about a current run.  It DOES enforce a valid configuration.
 *
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
public class LamarkConfig extends LamarkRuntimeParameters {
    /**
     * Handle to the creator component *
     */
    private Class<? extends ICreator> creatorClass;
    /**
     * Handle to the crossover component *
     */
    private Class<? extends ICrossover> crossoverClass;
    /**
     * Handle to the fitness function component *
     */
    private Class<? extends IFitnessFunction> fitnessFunctionClass;
    /**
     * Handle to the mutator component *
     */
    private Class<? extends IMutator> mutatorClass;
    /**
     * Handle to the selector component, defaulted to RouletteWheel *
     */
    // private Class<? extends ISelector> selectorClass = RouletteWheel.class;
    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    // private Class<? extends IIndividualFormatter> individualFormatterClass = DefaultIndividualFormatter.class;

    /**
     * Specific configuration for creator class
     */
    private Map<String, Object> creatorConfiguration;
    /**
     * Specific configuration for crossover class
     */
    private Map<String, Object> crossoverConfiguration;
    /**
     * Specific configuration for fitness function class
     */
    private Map<String, Object> fitnessFunctionConfiguration;
    /**
     * Specific configuration for mutator class
     */
    private Map<String, Object> mutatorConfiguration;
    /**
     * Specific configuration for selector class
     */
    private Map<String, Object> selectorConfiguration;
    /**
     * Specific configuration for individual formatter class
     */
    private Map<String, Object> individualFormatterConfiguration;

    /**
     * Classes to instantiate that listen for Lamark events
     */
    private List<Class<? extends LamarkEventListener>> customListeners = new LinkedList<Class<? extends LamarkEventListener>>();

    /**
     * Holds a list of individuals the user wishes to force insertion of, if any
     * NOTE: Using this feature REQUIRES that your individual class has a constructor of the form
     * public xxx(String param)
     */
    private List<String> preCreatedIndividuals = new LinkedList<String>();


    /**
     * Accessor method
     *
     * @return Class containing the property
     */
    public Class<? extends ICreator> getCreatorClass() {
        return creatorClass;
    }

    /**
     * Mutator method
     *
     * @param creatorClass new value for the property
     */
    public void setCreatorClass(Class<? extends ICreator> creatorClass) {
        this.creatorClass = creatorClass;
    }

    /**
     * Accessor method
     *
     * @return Class containing the property
     */
    public Class<? extends ICrossover> getCrossoverClass() {
        return crossoverClass;
    }

    /**
     * Mutator method
     *
     * @param crossoverClass new value for the property
     */
    public void setCrossoverClass(Class<? extends ICrossover> crossoverClass) {
        this.crossoverClass = crossoverClass;
    }

    /**
     * Accessor method
     *
     * @return Class containing the property
     */
    public Class<? extends IFitnessFunction> getFitnessFunctionClass() {
        return fitnessFunctionClass;
    }

    /**
     * Mutator method
     *
     * @param fitnessFunctionClass new value for the property
     */
    public void setFitnessFunctionClass(Class<? extends IFitnessFunction> fitnessFunctionClass) {
        this.fitnessFunctionClass = fitnessFunctionClass;
    }

    /**
     * Accessor method
     *
     * @return Class containing the property
     */
    public Class<? extends IMutator> getMutatorClass() {
        return mutatorClass;
    }

    /**
     * Mutator method
     *
     * @param mutatorClass new value for the property
     */
    public void setMutatorClass(Class<? extends IMutator> mutatorClass) {
        this.mutatorClass = mutatorClass;
    }

    /**
     * Accessor method
     *
     * @return Class containing the property
     *
    public Class<? extends ISelector> getSelectorClass() {
    return selectorClass;
    }

    /**
     * Mutator method
     *
     * @param selectorClass new value for the property
     *
    public void setSelectorClass(Class<? extends ISelector> selectorClass) {
    this.selectorClass = selectorClass;
    }

    /**
     * Accessor method
     *
     * @return Class containing the property
     *
    public Class<? extends IIndividualFormatter> getIndividualFormatterClass() {
    return individualFormatterClass;
    }

    /**
     * Mutator method
     *
     * @param individualFormatterClass new value for the property
    public void setIndividualFormatterClass(Class<? extends IIndividualFormatter> individualFormatterClass) {
    this.individualFormatterClass = individualFormatterClass;
    }*/

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getCreatorConfiguration() {
        return creatorConfiguration;
    }

    /**
     * Mutator method
     *
     * @param creatorConfiguration new value for the property
     */
    public void setCreatorConfiguration(Map<String, Object> creatorConfiguration) {
        this.creatorConfiguration = creatorConfiguration;
    }

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getCrossoverConfiguration() {
        return crossoverConfiguration;
    }

    /**
     * Mutator method
     *
     * @param crossoverConfiguration new value for the property
     */
    public void setCrossoverConfiguration(Map<String, Object> crossoverConfiguration) {
        this.crossoverConfiguration = crossoverConfiguration;
    }

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getFitnessFunctionConfiguration() {
        return fitnessFunctionConfiguration;
    }

    /**
     * Mutator method
     *
     * @param fitnessFunctionConfiguration new value for the property
     */
    public void setFitnessFunctionConfiguration(Map<String, Object> fitnessFunctionConfiguration) {
        this.fitnessFunctionConfiguration = fitnessFunctionConfiguration;
    }

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getMutatorConfiguration() {
        return mutatorConfiguration;
    }

    /**
     * Mutator method
     *
     * @param mutatorConfiguration new value for the property
     */
    public void setMutatorConfiguration(Map<String, Object> mutatorConfiguration) {
        this.mutatorConfiguration = mutatorConfiguration;
    }

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getSelectorConfiguration() {
        return selectorConfiguration;
    }

    /**
     * Mutator method
     *
     * @param selectorConfiguration new value for the property
     */
    public void setSelectorConfiguration(Map<String, Object> selectorConfiguration) {
        this.selectorConfiguration = selectorConfiguration;
    }

    /**
     * Accessor method
     *
     * @return Map containing the property
     */
    public Map<String, Object> getIndividualFormatterConfiguration() {
        return individualFormatterConfiguration;
    }

    /**
     * Mutator method
     *
     * @param individualFormatterConfiguration new value for the property
     */
    public void setIndividualFormatterConfiguration(Map<String, Object> individualFormatterConfiguration) {
        this.individualFormatterConfiguration = individualFormatterConfiguration;
    }

    /**
     * Accessor method
     *
     * @return List containing the property
     */
    public List<Class<? extends LamarkEventListener>> getCustomListeners() {
        return customListeners;
    }

    /**
     * Mutator method
     *
     * @param customListeners new value for the property
     */
    public void setCustomListeners(List<Class<? extends LamarkEventListener>> customListeners) {
        this.customListeners = customListeners;
    }

    /**
     * Accessor method
     *
     * @return List containing the property
     */
    public List<String> getPreCreatedIndividuals() {
        return preCreatedIndividuals;
    }

    /**
     * Mutator method
     *
     * @param preCreatedIndividuals new value for the property
     */
    public void setPreCreatedIndividuals(List<String> preCreatedIndividuals) {
        this.preCreatedIndividuals = preCreatedIndividuals;
    }

}


