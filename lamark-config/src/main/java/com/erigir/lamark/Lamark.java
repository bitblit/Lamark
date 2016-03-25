/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import com.erigir.lamark.config.LamarkRuntimeParameters;
import com.erigir.lamark.events.*;
import com.erigir.lamark.selector.RouletteWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;

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
     * Shared instance of the random class
     */
    private Random random = new Random();
    /**
     * Handle to the object will all the configurable settings for an instance
     */
    private LamarkRuntimeParameters runtimeParameters;

    // ---------------------------------------------------------
    // Configurable options
    // ---------------------------------------------------------
    // The basic building blocks of a GA
    /**
     * Handle to the creator component *
     */
    private ICreator creator;

    /**
     * Handle to the crossover component *
     */
    private ICrossover crossover;

    /**
     * Handle to the fitness function component *
     */
    private IFitnessFunction fitnessFunction;

    /**
     * Handle to the mutator component *
     */
    private IMutator mutator;

    /**
     * Handle to the selector component, defaulted to RouletteWheel *
     */
    private ISelector selector = new RouletteWheel(this);

    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    private IIndividualFormatter formatter = new DefaultIndividualFormatter();

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Set of objects to be notified of abortion events *
     */
    private Set<LamarkEventListener> abortListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified of new best individual events *
     */
    private Set<LamarkEventListener> betterIndividualFoundListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified when the last population is complete *
     */
    private Set<LamarkEventListener> lastPopulationCompleteListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified each time a population is completed *
     */
    private Set<LamarkEventListener> populationCompleteListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified if a uniform population (all individuals identical) is found *
     */
    private Set<LamarkEventListener> uniformPopulationListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified if an exception occurs during processing - typically the GA will abort*
     */
    private Set<LamarkEventListener> exceptionListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified if a log event occurs *
     */
    private Set<LamarkEventListener> logListeners = new HashSet<LamarkEventListener>();

    /**
     * Set of objects to be notified when the 'plan' for the next population is complete *
     */
    private Set<LamarkEventListener> planCompleteListeners = new HashSet<LamarkEventListener>();

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
        if (runtimeParameters.isTrackParentage() && child != null && parents != null) {
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
        if (!runtimeParameters.isTrackParentage()) {
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
        return formatter.format(i);
    }

    /**
     * Converts a collection of individuals into human-readable format
     *
     * @param c Collection to convert
     * @return String containing the human-readable format
     */
    public final String format(Collection<Individual> c) {
        return formatter.format(c);
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logSevere(Object o) {
        event(new LogEvent(this, o, Level.SEVERE));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logWarning(Object o) {
        event(new LogEvent(this, o, Level.WARNING));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logInfo(Object o) {
        event(new LogEvent(this, o, Level.INFO));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logConfig(Object o) {
        event(new LogEvent(this, o, Level.CONFIG));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logFine(Object o) {
        event(new LogEvent(this, o, Level.FINE));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logFiner(Object o) {
        event(new LogEvent(this, o, Level.FINER));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     *
     * @param o Object to log
     */
    public final void logFinest(Object o) {
        event(new LogEvent(this, o, Level.FINEST));
    }

    /**
     * Returns whether the passed population is the last one for any of a variety of reasons.
     *
     * @param p Population to test for "lastness"
     * @return true if this is a last population
     */
    private boolean lastPopulation(Population p) {
        return (runtimeParameters.getMaximumPopulations() != null && p != null && p.getNumber() >= runtimeParameters.getMaximumPopulations());
    }

    /**
     * Main method for the lamark instance, which configures and runs the GA.
     *
     * @see java.lang.Runnable#run()
     */
    public final Population call() {
        // Calc exit type and notify listeners
        LastPopulationCompleteEvent.Type exitType = LastPopulationCompleteEvent.Type.BY_POPULATION_NUMBER;

        startTime = new Long(System.currentTimeMillis());
        try {

            logInfo("top, ab=" + aborted);

            totalWaitTime = 0L;
            // Init the workpackage queue
            WorkPackage.initializeQueue(runtimeParameters.getPopulationSize());

            if (runtimeParameters.getRandomSeed() != null) {
                random.setSeed(runtimeParameters.getRandomSeed());
            }
            logInfo("Started Lamark run at time " + new Date());
            running = true;

            // Start a deadlock monitor
            new Thread(new DeadLockMonitor(this, Thread.currentThread())).start();

            int lowerElitismCount = (int) Math.ceil(runtimeParameters.getPopulationSize()
                    * runtimeParameters.getLowerElitism());
            logFine("Lower Elitism Percent=" + runtimeParameters.getLowerElitism() + " Size="
                    + runtimeParameters.getPopulationSize());
            logFine("Will replace the lower " + lowerElitismCount
                    + " individuals each generation");

            int upperElitismCount = (int) Math.ceil(runtimeParameters.getPopulationSize()
                    * runtimeParameters.getUpperElitism());
            logFine("Upper Elitism Percent=" + runtimeParameters.getUpperElitism() + " Size="
                    + runtimeParameters.getPopulationSize());
            logFine("Will reserve the upper " + upperElitismCount
                    + " individuals each generation");
            logFine("Will generate "
                    + (runtimeParameters.getPopulationSize() - (lowerElitismCount + upperElitismCount))
                    + " individuals by crossover each generation");

            Population prev = null;

            logInfo("ent ab=" + aborted);
            while (!lastPopulation(current) && !aborted && !targetScoreFound()) {
                // Init next generation
                prev = current;
                current = new Population(this, prev);

                int cForceInsert = 0;
                int cUpperElite = 0;
                int cLowerElite = 0;
                int cCrossover = 0;

                // Create the list of work packages
                // Step 1 - Insert any population members in the to-be-inserted
                // list, up to max size
                int remaining = runtimeParameters.getPopulationSize();
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
                    logInfo("Inserted " + toTake
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
                    logInfo("Retained " + upperEliteThisPop
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
                    logInfo("Created " + lowerEliteThisPop
                            + " to replace via lower elitism");
                }

                // Step 4 - Create any remnants via crossover (or create if this
                // is the first population)
                if (remaining > 0) {
                    logInfo("Creating " + remaining + " via crossover");
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
                logFine("For population " + current.getNumber()
                        + " best score is " + current.best().getFitness());
                logFinest("Population description:");
                logFinest(format(current.getIndividuals()));
                event(new PopulationCompleteEvent(
                        this, current));

                // Check for a new 'best'
                if ((currentBest == null)
                        || (current.best().getFitness() > currentBest.getFitness() && getFitnessFunction()
                        .fitnessType() == EFitnessType.MAXIMUM_BEST)
                        || (current.best().getFitness() < currentBest.getFitness() && getFitnessFunction()
                        .fitnessType() == EFitnessType.MINIMUM_BEST)) {
                    currentBest = current.best();
                    event(new BetterIndividualFoundEvent(this, current,
                            (Individual) current.best()));
                }

                // Check if the population is uniform
                if (current != null && current.isUniform()) {
                    event(new UniformPopulationEvent(this, current));
                    if (runtimeParameters.isAbortOnUniformPopulation()) {
                        aborted = true;
                    }
                }

            } // End main loop
            logInfo("exit, ab=" + aborted);

            // If we exited via abortion, notify listeners
            if (aborted) {
                if (abortListeners.size() > 0) {
                    event(new AbortedEvent(this));
                }
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
        if (currentBest != null && runtimeParameters.getTargetScore() != null) {
            if (fitnessFunction.fitnessType() == EFitnessType.MAXIMUM_BEST) {
                return currentBest.getFitness().compareTo(runtimeParameters.getTargetScore()) >= 0;
            } else // min best
            {
                return currentBest.getFitness().compareTo(runtimeParameters.getTargetScore()) <= 0;
            }
        } else {
            return false;
        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addLogListener(LamarkEventListener jel) {
        if (jel != null) {
            logListeners.add(jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addAbortListener(LamarkEventListener jel) {
        if (jel != null) {
            abortListeners.add(jel);
            logInfo("Added abort listener:" + jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addExceptionListener(LamarkEventListener jel) {
        if (jel != null) {
            exceptionListeners.add(jel);
            logInfo("Added exception listener:" + jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addBetterIndividualFoundListener(LamarkEventListener jel) {
        if (jel != null) {
            betterIndividualFoundListeners.add(jel);
            logInfo("Added BIF listener:" + jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addLastPopulationCompleteListener(LamarkEventListener jel) {
        if (jel != null) {
            lastPopulationCompleteListeners.add(jel);
            logInfo("Added last pop complete listener:" + jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addUniformPopulationListener(LamarkEventListener jel) {
        if (jel != null) {
            uniformPopulationListeners.add(jel);
            logInfo("Added uniform pop listener:" + jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addPopulationCompleteListener(LamarkEventListener jel) {
        if (jel != null) {
            populationCompleteListeners.add(jel);
            logInfo("Added pop complete listener:" + jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     *
     * @param jel LamarkEventListener to add
     */
    public final void addPopulationPlanCompleteListener(LamarkEventListener jel) {
        if (jel != null) {
            planCompleteListeners.add(jel);
            logInfo("Added pop plan complete listener:" + jel);

        }
    }

    /**
     * Registers this listener to receive ALL events
     *
     * @param jel LamarkEventListener to add
     */
    public final void addGenericListener(LamarkEventListener jel) {
        if (jel != null) {
            abortListeners.add(jel);
            betterIndividualFoundListeners.add(jel);
            lastPopulationCompleteListeners.add(jel);
            uniformPopulationListeners.add(jel);
            populationCompleteListeners.add(jel);
            exceptionListeners.add(jel);
            logListeners.add(jel);
            planCompleteListeners.add(jel);
            logInfo("Added generic listener:" + jel);
        }
    }


    /**
     * Sends the event to all appropriate listeners, or enques it if instance not started.
     *
     * @param event LamarkEvent to broadcast
     * @return true if the event was sent, false if it was enqueud
     */
    public final boolean event(LamarkEvent event) {
        if (event != null) {
            Set<LamarkEventListener> listenerSet = null;
            Class tClass = event.getClass();
            if (AbortedEvent.class.isAssignableFrom(tClass)) {
                listenerSet = abortListeners;
            } else if (BetterIndividualFoundEvent.class.isAssignableFrom(tClass)) {
                listenerSet = betterIndividualFoundListeners;
            } else if (ExceptionEvent.class.isAssignableFrom(tClass)) {
                listenerSet = exceptionListeners;
            } else if (LastPopulationCompleteEvent.class.isAssignableFrom(tClass)) {
                listenerSet = lastPopulationCompleteListeners;
            } else if (LogEvent.class.isAssignableFrom(tClass)) {
                listenerSet = logListeners;
            } else if (PopulationCompleteEvent.class.isAssignableFrom(tClass)) {
                listenerSet = populationCompleteListeners;
            } else if (PopulationPlanCompleteEvent.class.isAssignableFrom(tClass)) {
                listenerSet = planCompleteListeners;
            } else if (UniformPopulationEvent.class.isAssignableFrom(tClass)) {
                listenerSet = uniformPopulationListeners;
            }

            // Send it to the proper set
            for (LamarkEventListener listener : listenerSet) {
                listener.handleEvent(event);
            }
            return true;
        }
        return false;
    }

    /**
     * A function called by subunits (wp's basically) when an exception occurs.
     * Causes the GA to abort and an error message to be thrown and logged.
     *
     * @param e Exception that occured
     */
    public final void exceptionInSubunit(Exception e) {
        logSevere("Error occurred:" + e);
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
        if (running && null != startTime && null != current && null != runtimeParameters.getMaximumPopulations()) {
            long curTime = currentRuntimeMS();
            double pctDone = 0;
            if (runtimeParameters.getMaximumPopulations() != null) {
                pctDone = (double) getCurrentGenerationNumber()
                        / (double) runtimeParameters.getMaximumPopulations();
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
        return random.nextDouble() < runtimeParameters.getMutationProbability();
    }

    /**
     * Test whether crossover should occur.
     *
     * @return true if test succeeds, false otherwise.
     */
    public boolean crossoverFlip() {
        return random.nextDouble() < runtimeParameters.getCrossoverProbability();
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
     * @return ICrossover containing the property
     */
    public final ICrossover getCrossover() {
        return crossover;
    }

    /**
     * Mutator method
     *
     * @param pCrossover new value
     */
    public final void setCrossover(ICrossover pCrossover) {
        checkRunning();
        this.crossover = pCrossover;
        updateBackPointer(pCrossover);
    }

    /**
     * Accessor method
     *
     * @return IFitnessFunction containing the property
     */
    public final IFitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    /**
     * Mutator method
     *
     * @param pFitnessFunction new value
     */
    public final void setFitnessFunction(IFitnessFunction pFitnessFunction) {
        checkRunning();
        this.fitnessFunction = pFitnessFunction;
        updateBackPointer(pFitnessFunction);
    }

    /**
     * Accessor method
     *
     * @return IMutator containing the property
     */
    public final IMutator getMutator() {
        return mutator;
    }

    /**
     * Mutator method
     *
     * @param pMutator new value
     */
    public final void setMutator(IMutator pMutator) {
        checkRunning();
        this.mutator = pMutator;
        updateBackPointer(pMutator);
    }

    /**
     * Accessor method
     *
     * @return ISelector containing the property
     */
    public final ISelector getSelector() {
        return selector;
    }

    /**
     * Mutator method
     *
     * @param pSelector new value
     */
    public final void setSelector(ISelector pSelector) {
        checkRunning();
        this.selector = pSelector;
        updateBackPointer(pSelector);
    }

    /**
     * Accessor method
     *
     * @return ICreator containing the property
     */
    public final ICreator getCreator() {
        return creator;
    }

    /**
     * Mutator method
     *
     * @param pCreator new value
     */
    public final void setCreator(ICreator pCreator) {
        checkRunning();
        this.creator = pCreator;
        updateBackPointer(pCreator);
    }

    private void updateBackPointer(ILamarkComponent component) {
        if (component != null) {
            component.setLamark(this);
        }
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
     * @return IIndividualFormatter containing the property
     */
    public IIndividualFormatter getFormatter() {
        return formatter;
    }

    /**
     * Mutator method
     *
     * @param pFormatter new value
     */
    public void setFormatter(IIndividualFormatter pFormatter) {
        checkRunning();
        this.formatter = pFormatter;
    }

    /**
     * Accessor method
     *
     * @return LamarkRuntimeParameters containing the property
     */
    public LamarkRuntimeParameters getRuntimeParameters() {
        return runtimeParameters;
    }

    /**
     * Mutator method
     *
     * @param runtimeParameters new value
     */
    public void setRuntimeParameters(LamarkRuntimeParameters runtimeParameters) {
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
         * @see java.lang.Runnable#run()
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
                        subject.logFinest("Deadlock Check, no DL Found");
                    }
                    if (cycleCount > THRESHOLD) {
                        subject.logWarning("Deadlock detected, gen=" + current.getNumber() + " lock = " + " current=" + current + " cs=" + current.getSize() + " cts=" + current.getTargetSize() + " qs:" + WorkPackage.queueSize() + " main state:" + mainThread.getState());
                    }
                    Thread.sleep(1000);

                }
            } catch (Exception e) {
                LOG.error("Deadlock failure", e);
            }
        }
    }
}
