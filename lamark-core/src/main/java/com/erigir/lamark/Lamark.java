/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import com.erigir.lamark.annotation.*;
import com.erigir.lamark.annotation.LamarkEventListener;
import com.erigir.lamark.config.ERuntimeParameters;
import com.erigir.lamark.config.LamarkRuntimeParameters;
import com.erigir.lamark.events.*;
import com.erigir.lamark.selector.RouletteWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lamark is the central class that runs a generic genetic algorithm.
 * <p/>
 * Assuming you have already written any classes necessary to implement the 4 main
 * components (creator, crossover, fitness function, mutator), then the standard usage
 * of a Lamark object is as follows:
 * <ol>
 * <li>Create a lamark instance</li>
 * <li>Instantiate a copy of each of the 4 main components</li>
 * <li>Set any applicable parameters on each of the components</li>
 * <li>Set the component into Lamark using the appropriate set method</li>
 * <li>Optionally set the 5th component (selector) if this is useful for you</li>
 * <li>Optionally instantiate and set a custom individual formatter</li>
 * <li>Create a LamarkRuntimeParameters object and set it into the lamark instance</li>
 * <li>Instantiate and add any listeners as appropriate</li>
 * <li>Run Lamark
 * <ul><li>Either call the call() method on the instance to run within your current thread, <em>or</em></li>
 * <li>submit the lamark instance to an executor from java.util.concurrent,
 * <li>e.g., Executors.newSingleThreadExecutor().submit(lamark) If you use a new thread you can either</li>
 * <li>wait on the Future (its for this reason Lamark implements "callable" or listen for the correct</li>
 * <li>event.</li></ul></li>
 * <li>During the run (via events) or after the run (via cached data) perform analysis on gathered results.</li>
 * </ol>
 *
 * @author cweiss
 * @since 04/2006
 */
public class Lamark implements Callable<Population> {
    private static final Logger LOG = LoggerFactory.getLogger(Lamark.class);
    /**
     * Holder for all runtime parameters
     * @see com.erigir.lamark.config.ERuntimeParameters
     */
    private Map<String,Object> runtimeParameters = new TreeMap<>();
    /**
     * Shared instance of the random class
     */
    private Random random = new Random();

    // ---------------------------------------------------------
    // Configurable options
    // ---------------------------------------------------------
    // The basic building blocks of a GA
    /**
     * Handle to the creator component *
     */
    private DynamicMethodWrapper<Creator> creator;
    /**
     * Handle to the crossover component *
     */
    private DynamicMethodWrapper<Crossover> crossover;
    /**
     * Handle to the fitness function component *
     */
    private DynamicMethodWrapper<FitnessFunction> fitnessFunction;
    /**
     * Handle to the mutator component *
     */
    private DynamicMethodWrapper<Mutator> mutator;
    /**
     * Handle to the selector component, defaulted to RouletteWheel *
     */
    private ISelector selector = new RouletteWheel();
    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    private DynamicMethodWrapper<IndividualFormatter> formatter;
    /**
     * Handle to the preloader if any *
     */
    private DynamicMethodWrapper<PreloadIndividuals> preloader;

    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    private Set<DynamicMethodWrapper<LamarkEventListener>> listeners = new HashSet<>();

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Whether this lamark instance was aborted.  Defaults to false *
     */
    private boolean aborted = false;

    /**
     * System time when the instance started running *
     */
    private Long startTime;

    /**
     * True when the instance is currently running the GA *
     */
    private boolean running = false;

    /**
     * Amount of time the instance has run so far *
     */
    private Long totalRunTime;

    /**
     * Amount of time the instance has spent in the main thread, waiting for work packages to finish *
     */
    private Long totalWaitTime;

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Handle to the executorservice that will process all work packages *
     */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Holds a reference to the best individual found so far *
     */
    private Individual currentBest;

    /**
     * Holds a reference to the current population being created (also used for catching deadlocks) *
     */
    private Population current;

    /**
     * Holds the parentage of individuals, if parentage tracking is turned on *
     */
    private Map<Individual<?>, List<Individual<?>>> parentage = new HashMap<Individual<?>, List<Individual<?>>>();

    /**
     * List of individuals to be inserted at next opportunity
     * This is used either for directed search, or for communication across 'worlds'
     */
    private List<Individual> toBeInserted = new LinkedList<Individual>();

    public Lamark(Object toIntrospect)
    {
        super();
        // Introspect the object provided and setup from it
        setupViaIntrospection(toIntrospect);
    }

    public Lamark(Class clazz)
    {
        super();
        try {
            setupViaIntrospection(clazz.newInstance());
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    // TODO: add other selector options

    private void setupViaIntrospection(Object obj)
    {
        Class clz = obj.getClass();
        // Iterate over all the methods and find the key ones
        List<Method> paramGenerationMethods = AnnotationUtil.findMethodsByAnnotation(clz, Param.class);
        for (Method m:paramGenerationMethods)
        {
            Param p = m.getAnnotation(Param.class);
            if (m.getParameterTypes().length==0) {
                LOG.debug("Calling {} for parameter {}", m, p.value());
                if (Map.class.isAssignableFrom(m.getReturnType()))
                {
                    runtimeParameters.putAll((Map)Util.qExec(Map.class,obj, m));
                }
                else
                {
                    runtimeParameters.put(p.value(), Util.qExec(Object.class,obj, m));
                }
            }
            else
            {
                throw new IllegalArgumentException("Invalid param method "+m+" - no parameters allowed");
            }
        }

        LOG.info("Configuration : {}", runtimeParameters);

        Method createMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Creator.class, true);
        creator = new DynamicMethodWrapper<>(obj, createMethod, createMethod.getAnnotation(Creator.class));

        Method crossoverMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Crossover.class, true);
        crossover = new DynamicMethodWrapper<>(obj, crossoverMethod, crossoverMethod.getAnnotation(Crossover.class));

        Method fitnessFunctionMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, FitnessFunction.class, true);
        fitnessFunction = new DynamicMethodWrapper(obj,fitnessFunctionMethod, fitnessFunctionMethod.getAnnotation(FitnessFunction.class));

        Method formatMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, IndividualFormatter.class, false);
        if (formatMethod==null) {
            LOG.info("No format method defined, using default");
            DefaultIndividualFormatter def = new DefaultIndividualFormatter();
            formatMethod = AnnotationUtil.findSingleMethodByAnnotation(DefaultIndividualFormatter.class, IndividualFormatter.class);
            formatter = new DynamicMethodWrapper(def, formatMethod, formatMethod.getAnnotation(IndividualFormatter.class));
        }
        else {
            formatter = new DynamicMethodWrapper(obj, formatMethod, formatMethod.getAnnotation(IndividualFormatter.class));
        }

        Method mutateMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Mutator.class, true);
        mutator = new DynamicMethodWrapper(obj, mutateMethod, mutateMethod.getAnnotation(Mutator.class));

        Method preloadMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, PreloadIndividuals.class, false);
        if (preloadMethod!=null) {
            preloader = new DynamicMethodWrapper(obj, preloadMethod, preloadMethod.getAnnotation(PreloadIndividuals.class));
        }

        List<Method> listenerMethods = AnnotationUtil.findMethodsByAnnotation(clz, LamarkEventListener.class);
        LOG.info("Found {} listener methods", listenerMethods.size());
        for (Method m:listenerMethods)
        {
            listeners.add(new DynamicMethodWrapper(obj, m, m.getAnnotation(LamarkEventListener.class)));
        }

        // Validate the parameters
        ERuntimeParameters.validate(runtimeParameters);

        // If we made it here, build the random object
        Long randSeed = ERuntimeParameters.RANDOM_SEED.read(runtimeParameters, Long.class);
        randSeed = (randSeed==null)?System.currentTimeMillis():randSeed;
        Random random = new Random(randSeed);
        runtimeParameters.put("random",random);
        runtimeParameters.put("lamark", this); // TODO: Is this really a good idea?
        runtimeParameters.put("fitnessType", getFitnessType());

        // Make the selector use the same RNG
        selector.initialize(random,getFitnessType());

    }

    /**
     * Records the parents for a given child in the parent registry.
     * <p/>
     * NOTE: This only takes place if trackParentage is true.  Otherwise
     * nothing happens when this is called.
     *
     * @param child   Individual of which  to register parents
     * @param parents List of parents for the child
     */
    public void registerParentage(Individual<?> child, List<Individual<?>> parents) {
        if (ERuntimeParameters.TRACK_PARENTAGE.read(runtimeParameters, Boolean.class) && child != null && parents != null) {
            parentage.put(child, parents);
        }
    }

    /**
     * Get a list of parents for the passed individual.
     * If trackParentage is turned on, and this child was created at some
     * point in the current instance, than this function will return a list
     * of the childs immediate parents.  Call recursively to build a
     * "family tree" of a given individual.
     *
     * @param child Individual to get the parents for
     * @return List of Individual parent objects
     */
    public List<Individual<?>> getParentage(Individual child) {
        if (!ERuntimeParameters.TRACK_PARENTAGE.read(runtimeParameters, Boolean.class)) {
            throw new IllegalStateException("Can't call 'getParentage' if trackParentage is off");
        }
        if (child == null) {
            throw new IllegalArgumentException("Can't call 'getParentage' on null child");
        } else {
            return parentage.get(child);
        }
    }

    /**
     * Puts the individual on the list to be inserted at the next opportunity.
     *
     * @param i Individual to be inserted.
     */
    public final void enqueueForInsert(Individual i) {
        toBeInserted.add(i);
    }

    /**
     * Removes everything from the list of "to be inserted" individuals.
     */
    public final void clearInsertQueue() {
        toBeInserted.retainAll(Collections.EMPTY_LIST);
    }


    /**
     * Called by all property setters to make sure that a property isn't modified as the system is running.
     *
     * @throws IllegalStateException if running is true
     */
    public final void checkRunning() {
        if (running) {
            throw new IllegalStateException(
                    "Cannot modify this property while Lamark is running.");
        }
    }

    /**
     * Accessor method (READ-ONLY)
     *
     * @return boolean containing the property
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Converts the passed individual into a human-readable string using the formatter.
     *
     * @param i Individual to convert
     * @return String containing the human-readable version
     */
    public final String format(Individual i) {
        return formatter.execute(String.class,i.getGenome());
    }

    /**
     * Converts a collection of individuals into human-readable format
     *
     * @param c Collection to convert
     * @return String containing the human-readable format
     */
    public final String format(Collection<Individual> c) {
        
        List<String> l = new ArrayList<>(c.size());
        for (Individual i:c)
        {
            l.add(format(i));
        }
        return l.toString();
    }

    public EFitnessType getFitnessType()
    {
        return fitnessFunction.getKeyAnnotation().fitnessType();
    }

    /**
     * Returns whether the passed population is the last one for any of a variety of reasons.
     *
     * @param p Population to test for "lastness"
     * @return true if this is a last population
     */
    private boolean lastPopulation(Population p) {
        return (ERuntimeParameters.MAXIMUM_POPULATIONS.read(runtimeParameters, Integer.class) != null && p != null && p.getNumber() >= ERuntimeParameters.MAXIMUM_POPULATIONS.read(runtimeParameters, Integer.class));
    }

    /**
     * Main method for the lamark instance, which configures and runs the GA.
     *
     * @see Runnable#run()
     */
    public final Population call() {
        // Calc exit type and notify listeners
        LastPopulationCompleteEvent.Type exitType = LastPopulationCompleteEvent.Type.BY_POPULATION_NUMBER;

        startTime = new Long(System.currentTimeMillis());
        try {

            LOG.info("top, ab=" + aborted);

            totalWaitTime = 0L;
            // Init the workpackage queue
            WorkPackage.initializeQueue(ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class));

            if (ERuntimeParameters.RANDOM_SEED.read(runtimeParameters, Long.class) != null) {
                random.setSeed(ERuntimeParameters.RANDOM_SEED.read(runtimeParameters, Long.class));
            }
            LOG.info("Started Lamark run at time " + new Date());
            running = true;

            // Start a deadlock monitor
            new Thread(new DeadLockMonitor(this, Thread.currentThread())).start();

            int lowerElitismCount = (int) Math.ceil(ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class) 
                    * ERuntimeParameters.LOWER_ELITISM.read(runtimeParameters, Double.class));
            LOG.debug("Lower Elitism Percent=" + ERuntimeParameters.LOWER_ELITISM.read(runtimeParameters, Double.class) + " Size="
                    + ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class));
            LOG.debug("Will replace the lower " + lowerElitismCount
                    + " individuals each generation");

            int upperElitismCount = (int) Math.ceil(ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class)
                    * ERuntimeParameters.UPPER_ELITISM.read(runtimeParameters, Double.class));
            LOG.debug("Upper Elitism Percent=" + ERuntimeParameters.UPPER_ELITISM.read(runtimeParameters, Double.class) + " Size="
                    + ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class));
            LOG.debug("Will reserve the upper " + upperElitismCount
                    + " individuals each generation");
            LOG.debug("Will generate "
                    + (ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class) - (lowerElitismCount + upperElitismCount))
                    + " individuals by crossover each generation");

            Population prev = null;

            LOG.info("ent ab=" + aborted);
            while (!lastPopulation(current) && !aborted && !targetScoreFound()) {
                // Init next generation
                prev = current;
                current = new Population(getFitnessType(), ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class), prev);

                int cForceInsert = 0;
                int cUpperElite = 0;
                int cLowerElite = 0;
                int cCrossover = 0;

                // Create the list of work packages
                // Step 1 - Insert any population members in the to-be-inserted
                // list, up to max size
                int remaining = ERuntimeParameters.POPULATION_SIZE.read(runtimeParameters, Integer.class);
                int toTake = Math.min(remaining, toBeInserted.size());

                if (toTake > 0) {
                    List<Individual<?>> insert = new LinkedList<Individual<?>>();
                    for (int i = 0; i < toTake; i++) {
                        insert.add(toBeInserted.remove(0));
                    }
                    for (WorkPackage w : WorkPackage.copies(this, current,
                            insert)) {
                        submitWorkPackage(w, getCurrentGenerationNumber());
                    }
                    LOG.info("Inserted " + toTake
                            + " from the 'tobeinserted' queue");
                    remaining -= toTake;
                    cForceInsert = toTake;
                }

                // Step 2 - Insert any upper elitism retained from previous gen
                int upperEliteThisPop = Math.min(upperElitismCount, remaining);
                if (prev != null && upperEliteThisPop > 0) {
                    List<Individual<?>> elite = prev.getIndividuals()
                            .subList(0, upperEliteThisPop);
                    for (WorkPackage w : WorkPackage.copies(this, current,
                            elite)) {
                        submitWorkPackage(w, getCurrentGenerationNumber());
                    }
                    remaining -= upperEliteThisPop;
                    cUpperElite = upperEliteThisPop;
                    LOG.info("Retained " + upperEliteThisPop
                            + " via upper elitism");
                }

                // Step 3 - Replace any via lower elitism
                int lowerEliteThisPop = Math.min(lowerElitismCount, remaining);
                if (lowerEliteThisPop > 0) {
                    for (WorkPackage w : WorkPackage.newItems(this, current,
                            lowerEliteThisPop)) {
                        submitWorkPackage(w, getCurrentGenerationNumber());
                    }
                    remaining -= lowerEliteThisPop;
                    cLowerElite = lowerEliteThisPop;
                    LOG.info("Created " + lowerEliteThisPop
                            + " to replace via lower elitism");
                }

                // Step 4 - Create any remnants via crossover (or create if this
                // is the first population)
                if (remaining > 0) {
                    LOG.info("Creating " + remaining + " via crossover");
                    cCrossover = remaining;
                    if (prev != null) {
                        for (WorkPackage w : WorkPackage.crossovers(this, prev,
                                current, remaining)) {
                            submitWorkPackage(w, getCurrentGenerationNumber());
                        }
                        remaining = 0;
                    } else {
                        for (WorkPackage w : WorkPackage.newItems(this,
                                current, remaining)) {
                            submitWorkPackage(w, getCurrentGenerationNumber());
                        }
                        remaining = 0;
                    }

                }

                event(new PopulationPlanCompleteEvent(this, cForceInsert, cUpperElite, cLowerElite, cCrossover));

                // Step 5 - wait on the population to complete
                while (!current.isFilled()) {
                    current.startWait();
                }

                // Send the new population message
                LOG.debug("For population " + current.getNumber()
                        + " best score is " + current.best().getFitness());
                LOG.debug("Population description:");
                LOG.debug(format(current.getIndividuals()));
                event(new PopulationCompleteEvent(
                        this, current));

                // Check for a new 'best'
                if ((currentBest == null)
                        || (current.best().getFitness() > currentBest.getFitness() && getFitnessType() == EFitnessType.MAXIMUM_BEST)
                        || (current.best().getFitness() < currentBest.getFitness() && getFitnessType() == EFitnessType.MINIMUM_BEST)) {
                    currentBest = current.best();
                    event(new BetterIndividualFoundEvent(this, current,
                            (Individual) current.best()));
                }

                // Check if the population is uniform
                if (current != null && current.isUniform()) {
                    event(new UniformPopulationEvent(this, current));
                    if (ERuntimeParameters.ABORT_ON_UNIFORM_POPULATION.read(runtimeParameters, Boolean.class)) {
                        aborted = true;
                    }
                }

            } // End main loop
            LOG.info("exit, ab=" + aborted);

            // If we exited via abortion, notify listeners
            if (aborted) {
                event(new AbortedEvent(this));
            }
            if (current != null && current.isUniform()) {
                exitType = LastPopulationCompleteEvent.Type.UNIFORM;
            } else if (targetScoreFound()) {
                exitType = LastPopulationCompleteEvent.Type.BY_TARGET_SCORE;
            } else if (aborted) {
                exitType = LastPopulationCompleteEvent.Type.ABORTED;
            }

        } catch (Throwable t) {
            event(new ExceptionEvent(this, t));
            aborted = true;
        } finally {
            totalRunTime = new Long(System.currentTimeMillis() - startTime);
            startTime = null;

            // Notify listeners of last population
            event(new LastPopulationCompleteEvent(this, current, exitType));
            // Finally, mark running complete
            running = false;

        }
        return current;
    }

    /**
     * Returns the current generation number, or -1 if not started.
     *
     * @return long containing the number
     */
    public long getCurrentGenerationNumber() {
        if (current != null) {
            return current.getNumber();
        }
        return -1;
    }

    /**
     * Placeholder method for submitting work packages to be processed.
     *
     * @param wp    WorkPackage to submit
     * @param genNo Long number of the population this is submitted for
     */
    private void submitWorkPackage(WorkPackage wp, Long genNo) {
        // Placeholder in case i want to monitor every time a WP is submitted
        executor.submit(wp);
    }


    /**
     * Determine whether we have reached the target score, if any.
     *
     * @return true if we have
     */
    public boolean targetScoreFound() {
        if (currentBest != null && ERuntimeParameters.TARGET_SCORE.read(runtimeParameters, Double.class) != null) {
            if (getFitnessType() == EFitnessType.MAXIMUM_BEST) {
                return currentBest.getFitness().compareTo(ERuntimeParameters.TARGET_SCORE.read(runtimeParameters, Double.class)) >= 0;
            } else // min best
            {
                return currentBest.getFitness().compareTo(ERuntimeParameters.TARGET_SCORE.read(runtimeParameters, Double.class)) <= 0;
            }
        } else {
            return false;
        }
    }



    /**
     * Sends the event to all appropriate listeners, or enques it if instance not started.
     *
     * @param event LamarkEvent to broadcast
     * @return true if the event was sent, false if it was enqueud
     */
    public final boolean event(LamarkEvent event) {
        boolean rval = false;
        if (event != null) {
            for (DynamicMethodWrapper dmw:listeners)
            {
                Class param = dmw.getMethod().getParameterTypes()[0];
                if (param.isAssignableFrom(event.getClass()))
                {
                    dmw.execute(Object.class,event); // Dont care about return value
                    rval = true;
                }
            }
        }
        return rval;
    }

    /**
     * A function called by subunits (wp's basically) when an exception occurs.
     * Causes the GA to abort and an error message to be thrown and logged.
     *
     * @param e Exception that occured
     */
    public final void exceptionInSubunit(Exception e) {
        LOG.error("Error occurred:" + e);
        event(new ExceptionEvent(this, e));
        abort();
    }

    /**
     * Sets the abort flag, which causes the GA to stop at the end of the current generation.
     */
    public final void abort() {
        this.aborted = true;
    }


    /**
     * Returns the time elapsed since instace start in milliseconds.
     *
     * @return long containing the runtime
     */
    public final long currentRuntimeMS() {
        if (running && null != startTime) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    /**
     * Returns the estimated time from start to completion milliseconds.
     * NOTE: this function only returns a value if maximumPopulations is
     * set.
     *
     * @return long containing the estimated runtime
     */
    public final long estimatedRuntimeMS() {
        if (running && null != startTime && null != current && null != ERuntimeParameters.MAXIMUM_POPULATIONS.read(runtimeParameters, Integer.class)) {
            long curTime = currentRuntimeMS();
            double pctDone = 0;
            if (ERuntimeParameters.MAXIMUM_POPULATIONS.read(runtimeParameters, Integer.class) != null) {
                pctDone = (double) getCurrentGenerationNumber()
                        / (double) ERuntimeParameters.MAXIMUM_POPULATIONS.read(runtimeParameters, Integer.class);
            }
            double totalTime = curTime / pctDone;
            return (long) (totalTime - curTime);
        }
        return 0;
    }

    /**
     * Test whether mutation should occur.
     *
     * @return true if test succeeds, false otherwise.
     */
    public boolean mutationFlip() {
        return random.nextDouble() < ERuntimeParameters.MUTATION_PROBABILITY.read(runtimeParameters, Double.class);
    }

    /**
     * Test whether crossover should occur.
     *
     * @return true if test succeeds, false otherwise.
     */
    public boolean crossoverFlip() {
        return random.nextDouble() < ERuntimeParameters.CROSSOVER_PROBABILITY.read(runtimeParameters, Double.class);
    }

    /**
     * Accessor method
     * NOTE: only non-null when the system is running
     *
     * @return Long containing the property
     */
    public final Long getTotalRunTime() {
        return totalRunTime;
    }

    /**
     * Accessor method
     * NOTE: only non-null after starting
     *
     * @return Individual containing the property
     */
    public final Individual getCurrentBest() {
        return currentBest;
    }

    /**
     * Accessor method
     *
     * @return Long containing the property
     */
    public Long getTotalWaitTime() {
        return totalWaitTime;
    }

    /**
     * Returns, on average, how much time is spent waiting on work package completion.
     * NOTE: Only non-null after the system is started
     *
     * @return double containing the property
     */
    public Double getAverageWaitTime() {
        if (current != null) {
            return totalWaitTime.doubleValue() / (double) getCurrentGenerationNumber();
        }
        return null;
    }

    /**
     * Returns the percentage of total run time that is consumed waiting on work package completion.
     * NOTE: Only non-null when the system is running
     *
     * @return Double containing the percentage
     */
    public Double getPercentageTimeWaiting() {
        if (getTotalRunTime() != null) {
            return getTotalWaitTime().doubleValue() / getTotalRunTime().doubleValue();
        }
        return null;
    }

    /**
     * Accessor method
     *
     * @return Random containing the property
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Mutator method
     *
     * @param random new value
     */
    public void setRandom(Random random) {
        checkRunning();
        this.random = random;
    }

    /**
     * Accessor method
     *
     * @return LamarkRuntimeParameters containing the property
     */
    public Map<String,Object> getRuntimeParameters() {
        return runtimeParameters;
    }

    /**
     * Mutator method
     *
     * @param runtimeParameters new value
     */
    public void setRuntimeParameters(Map<String,Object> runtimeParameters) {
        checkRunning();
        this.runtimeParameters = runtimeParameters;
    }

    /**
     * Accessor method
     *
     * @return LamarkConfig containing the property
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Mutator method
     *
     * @param executor new value
     */
    public void setExecutor(ExecutorService executor) {
        checkRunning();
        this.executor = executor;
    }

    public DynamicMethodWrapper<Creator> getCreator() {
        return creator;
    }

    public DynamicMethodWrapper<Crossover> getCrossover() {
        return crossover;
    }

    public DynamicMethodWrapper<FitnessFunction> getFitnessFunction() {
        return fitnessFunction;
    }

    public DynamicMethodWrapper<Mutator> getMutator() {
        return mutator;
    }

    public ISelector getSelector() {
        return selector;
    }

    public DynamicMethodWrapper<IndividualFormatter> getFormatter() {
        return formatter;
    }

    public DynamicMethodWrapper<PreloadIndividuals> getPreloader() {
        return preloader;
    }

    public Set<DynamicMethodWrapper<LamarkEventListener>> getListeners() {
        return listeners;
    }

    /**
     * A little inner class to run and make sure the GA hasn't hung up somewhere.
     * <p/>
     * Currently this class does little except send out messages when it thinks
     * a deadlock has occurred.  Probably should be extended to allow forced
     * abortion of the parent threads, as well as configurability on how
     * long to wait before assuming deadlock
     *
     * @author cweiss
     * @since 10/2007
     */
    class DeadLockMonitor implements Runnable {

        /**
         * Number of times I need to see the same generation before I assume deadlock *
         */
        private static final int THRESHOLD = 4;
        /**
         * Lamark object to monitor *
         */
        private Lamark subject;
        /**
         * Last generation number seen when awake *
         */
        private Long lastGenerationNumber;
        /**
         * Number of times Ive seen that generation when I awoke *
         */
        private int cycleCount;
        /**
         * Handle to the main thread running lamark *
         */
        private Thread mainThread;

        /**
         * Default constructor
         *
         * @param toMonitor   Lamark to monitor for deadlock
         * @param pMainThread Thread to monitor for deadlock
         */
        public DeadLockMonitor(Lamark toMonitor, Thread pMainThread) {
            super();
            subject = toMonitor;
            mainThread = pMainThread;
        }

        /**
         * Checks the lamark instance to make sure it isnt hung up.
         *
         * @see Runnable#run()
         */
        public void run() {
            try {
                cycleCount = 0;
                lastGenerationNumber = subject.getCurrentGenerationNumber();
                while (subject != null && subject.running) {
                    if (lastGenerationNumber == subject.getCurrentGenerationNumber()) {
                        cycleCount++;
                    } else {
                        lastGenerationNumber = subject.getCurrentGenerationNumber();
                        cycleCount = 0;
                        subject.LOG.debug("Deadlock Check, no DL Found");
                    }
                    if (cycleCount > THRESHOLD) {
                        subject.LOG.warn("Deadlock detected, gen=" + current.getNumber() + " lock = " + " current=" + current + " cs=" + current.getSize() + " cts=" + current.getTargetSize() + " qs:" + WorkPackage.queueSize() + " main state:" + mainThread.getState());
                    }
                    Thread.sleep(1000);

                }
            } catch (Exception e) {
                LOG.error("Deadlock failure", e);
            }
        }
    }
}
