/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.erigir.lamark.events.AbortedEvent;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.ConfigurationEvent;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import com.erigir.lamark.events.LogEvent;
import com.erigir.lamark.events.PopulationCompleteEvent;
import com.erigir.lamark.events.PopulationPlanCompleteEvent;
import com.erigir.lamark.events.UniformPopulationEvent;
import com.erigir.lamark.selector.RouletteWheel;

/**
 * Lamark is the central class that runs a generic genetic algorithm.
 * 
 * Assuming you have already written any classes necessary to implement the 4 main 
 * components (creator, crossover, fitness function, mutator), then the standard usage 
 * of a Lamark object is as follows:
 * <ol>
 * <li>Create a lamark instance</li>
 * <li>Instantiate a copy of each of the 4 main components</li>
 * <li>Set any applicable parameters on each of the compoenents</li>
 * <li>Set the component into lamark using the appropriate set method</li>
 * <li>Optionally set the 5th component (selector) if this is useful for you</li>
 * <li>Optionally instantiate and set a custom individual formatter</li>
 * <li>Set the size of the population</li>
 * <li>Set any of the optional parameters as appropriate. See the lamark object properties for a list.</li>
 * <li>Instantiate and add any listeners as appropriate</li>
 * <li>Either call the run() method on the instance to run within your current thread, <em>or</em></li>
 * <li>call new Thread(lamark).start() to run the instance within a new thread (note that using this method, you should use a listener to know when lamark finishes)</li>
 * <li>During the run (via events) or after the run (via cached data) perform analysis on gathered results.</li>
 * </ol>
 * 
 * @author cweiss
 * @since 04/2006
 */
public class Lamark implements Runnable
{
    // ---------------------------------------------------------
    // Configurable options
    // ---------------------------------------------------------
    // The basic building blocks of a GA
    /** Handle to the creator component **/
    private ICreator creator;

    /** Handle to the crossover component **/
    private ICrossover crossover;

    /** Handle to the fitness function component **/
    private IFitnessFunction fitnessFunction;

    /** Handle to the mutator component **/
    private IMutator mutator;

    /** Handle to the selector component, defaulted to RouletteWheel **/
    private ISelector selector = new RouletteWheel(this);
    
    /** Handle to the individual formatter, used for printing individuals into messages **/
    private IIndividualFormatter formatter = new DefaultIndividualFormatter();

    /** Maximum number of generations to run.  If null, won't terminate due to generation number **/
    private Integer maximumPopulations;

    /** Number of individuals to have in a given generation.  REQUIRED PROPERTY **/
    private Integer populationSize;

    /** Percentage of individuals (0.0 - 1.0) to retain each generation as upper elitism.  NOTE: this number is multiplied by populationSize and the result is truncated to an integer **/
    private Double upperElitism = 0.0;

    /** Percentage of individuals (0.0 - 1.0) to replace each generation as lower elitism.  NOTE: this number is multiplied by populationSize and the result is truncated to an integer **/
    private Double lowerElitism = 0.0;

    /** Likelihood (0.0 - 1.0) of an individual being generated via crossover.  If the check fails, the first parent is copied instead. **/
    private Double crossoverProbability = 1.0;

    /** Likelihood (0.0 - 1.0) of an individual being mutated.  This is on an individual by individual basis, not a population-wide basis.  Individuals retained via upper elitism are
     * immune to mutation. **/
    private Double mutationProbability = 0.005;

    /** Number of threads to use to process work packages.  Defaults to 1. **/
    private Integer numberOfWorkerThreads = 1;
    
    /** A score which, if reached, will cause the algorthim to terminate.  If null, the algorithm will never terminate due to score **/
    private Double targetScore;
    
    /** Determines whether to track the parents of each individual.  Use with care, as turning this on will consume large amounts of memory in a short
     * period of time **/
    private boolean trackParentage = false;
    
    /** Determines whether the algorith should stop if it ever reaches the state of all the individuals being equals.  Defaults to true **/
    private boolean abortOnUniformPopulation = true;
    
    /** Seed to use for the random number generator.  Defaults to system current time.  Set to a constant to generate reproducible runs.  NOTE: runs
     * are only reproducible if this is a constant AND the number of worker threads is 1, since otherwise the order of processing is dependant on 
     * OS scheduling **/
    private Long randomSeed;
    
    /** Handle to the random object used to generate new numbers **/
    private Random random = new Random();

    /** Holds a list of individuals the user wishes to force insertion of, if any **/
    private List < Individual < ? >> toBeInserted = Collections.synchronizedList(new LinkedList < Individual < ? >>());

    // ----------------------------------------------------------------------------------------------------------------------------------
    /** Set of objects to be notified of abortion events **/
    private Set < LamarkEventListener > abortListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified of new best individual events **/
    private Set < LamarkEventListener > betterIndividualFoundListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified when the last population is complete **/
    private Set < LamarkEventListener > lastPopulationCompleteListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified each time a population is completed **/
    private Set < LamarkEventListener > populationCompleteListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified if a uniform population (all individuals identical) is found **/
    private Set < LamarkEventListener > uniformPopulationListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified if an exception occurs during processing - typically the GA will abort**/
    private Set < LamarkEventListener > exceptionListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified if a log event occurs **/
    private Set < LamarkEventListener > logListeners = new HashSet < LamarkEventListener >();

    /** Set of objects to be notified when the 'plan' for the next population is complete **/
    private Set < LamarkEventListener > planCompleteListeners = new HashSet < LamarkEventListener >();
    
    /** Set of objects to be notified when a configuration event occurs **/
    private Set <LamarkEventListener > configurationListeners = new HashSet <LamarkEventListener>();

    // ----------------------------------------------------------------------------------------------------------------------------------
    /** Whether this lamark instance was aborted.  Defaults to false **/
    private boolean aborted = false;

    /** System time when the instance started running **/
    private Long startTime;

    /** True when the instance is currently running the GA **/
    private boolean running = false;

    /** Amount of time the instance has run so far **/
    private Long totalRunTime;
    
    /** Amount of time the instance has spent in the main thread, waiting for work packages to finish **/
    private Long totalWaitTime;

    // ----------------------------------------------------------------------------------------------------------------------------------
    /** Handle to the executorservice that will process all work packages **/ 
    private ExecutorService executor; 

    // ----------------------------------------------------------------------------------------------------------------------------------
    /** Holds a reference to the best individual found so far **/
    private Individual currentBest; 
    
    /** Holds a reference to the current population being created (also used for catching deadlocks) **/
    private Population current;

    /** Holds the parentage of individuals, if parentage tracking is turned on **/
    private Map<Individual<?>,List<Individual<?>>> parentage = new HashMap<Individual<?>, List<Individual<?>>>();
    
    /** Holds the list of events that happened prior to the start of the instance.  
     * NOTE: This is here to allow clients to add components and listeners in any
     * order, and still get the events back.  It has as a side effect that any
     * configuration errors aren't realized and sent until the instance is started 
     **/
    private List<LamarkEvent> preStartEvents = new LinkedList<LamarkEvent>();

    /**
     * Records the parents for a given child in the parent registry.
     * 
     * NOTE: This only takes place if trackParentage is true.  Otherwise
     * nothing happens when this is called.
     * 
     * @param child Individual of which  to register parents
     * @param parents List of parents for the child
     **/
    public void registerParentage(Individual<?> child,List<Individual<?>> parents)
    {
        if (trackParentage && child!=null && parents!=null)
        {
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
    public List<Individual<?>> getParentage(Individual child)
    {
        if (!trackParentage)
        {
            throw new IllegalStateException("Can't call 'getParentage' if trackParentage is off");
        }
        if (child==null)
        {
            throw new IllegalArgumentException("Can't call 'getParentage' on null child");
        }
        else
        {
            return parentage.get(child);
        }
    }
    
    /**
     * Validates the given component, if it implements IValidatable.
     * @param comp Component to validate
     * @param errors List of errors, which the component can then add to if there is an error
     */
    private void conditionalValidate(ILamarkComponent comp,List<String> errors)
    {
        if (IValidatable.class.isInstance(comp))
        {
            ((IValidatable)comp).validate(errors);
        }
    }
    
    /**
     * Generate a list of any configuration problems that will prevent starting this instance.
     * @return List of string errors 
     */
    public List < String > getConfigurationErrors()
    {
        List < String > errors = new LinkedList < String >();

        if (fitnessFunction == null)
        {
            errors.add("Fitness function undefined");
        }
        else
        { 
            conditionalValidate(fitnessFunction,errors);
        }
        if (crossover == null)
        {
            errors.add("Crossover undefined");
        }
        else
        {
            conditionalValidate(crossover,errors);
        }

        if (selector == null)
        {
            errors.add("Selector undefined");
        }
        else
        {
            conditionalValidate(selector,errors);
        }

        if (creator == null)
        {
            errors.add("Creator undefined");
        }
        else
        {
            conditionalValidate(creator,errors);
        }

        if (executor == null)
        {
            errors.add("No pool size specified");
        }
        if (exceptionListeners.size()==0)
        {
            errors.add("No exception listeners attached");
        }
        if (populationSize==null)
        {
            errors.add("No population size set");
        }
        if (upperElitism==null)
        {
            errors.add("Upper elitism set to null (leave at 0.0, if that's what you want)");
        }
        if (lowerElitism==null)
        {
            errors.add("Lower elitism set to null (leave at 0.0, if that's what you want)");
        }
        if (crossoverProbability==null)
        {
            errors.add("Crossover probability set to null");
        }
        if (mutationProbability==null)
        {
            errors.add("Mutation probability set to null (set to 0.0, if that's what you want)");
        }
        if (numberOfWorkerThreads==null)
        {
            errors.add("Number of worker threads set to null (set to 1 for single-threading)");
        }

        // Null individual size is NOT an error (a given algorithm might not use it, and should mention it in its validate method if needed */
        // Null mutator is NOT an error
        // Null maximum populations is NOT an error
        return errors;
    }

    /**
     * Accessor method
     * @return double containing the property
     */
    public final double getCrossoverProbability()
    {
        return crossoverProbability;
    }

    /**
     * Mutator method
     * @param crossoverProbability new value for the property
     */
    public final void setCrossoverProbability(double crossoverProbability)
    {
        checkRunning();
        this.crossoverProbability = crossoverProbability;
    }

    /**
     * Accessor method
     * @return double containing the property
     */
    public final double getMutationProbability()
    {
        return mutationProbability;
    }

    /**
     * Mutator method
     * @param mutationProbability new value
     */
    public final void setMutationProbability(double mutationProbability)
    {
        checkRunning();
        this.mutationProbability = mutationProbability;
    }

    /**
     * Puts the individual on the list to be inserted at the next opportunity.
     * @param i Individual to be inserted.
     */
    public final void enqueueForInsert(Individual i)
    {
        toBeInserted.add(i);
    }

    /**
     * Removes everything from the list of "to be inserted" individuals.
     */
    public final void clearInsertQueue()
    {
        toBeInserted.retainAll(Collections.EMPTY_LIST);
    }
    
    
    /**
     * Called by all property setters to make sure that a property isn't modified as the system is running.
     * @throws IllegalStateException if running is true
     */
    public final void checkRunning()
    {
        if (running)
        {
            throw new IllegalStateException(
                "Cannot modify this property while Lamark is running.");
        }
    }

    /**
     * Accessor method
     * @return double containing the property
     */
    public final double getLowerElitism()
    {
        return lowerElitism;
    }

    /**
     * Mutator method
     * @param lowerElitism new value
     */
    public final void setLowerElitism(double lowerElitism)
    {
        checkRunning();
        this.lowerElitism = lowerElitism;
    }

    /**
     * Accessor method
     * @return double containing the property
     */
    public final double getUpperElitism()
    {
        return upperElitism;
    }

    /**
     * Mutator method
     * @param upperElitism new value
     */
    public final void setUpperElitism(double upperElitism)
    {
        checkRunning();
        this.upperElitism = upperElitism;
    }

    /**
     * Accessor method
     * @return Integer containing the property
     */
    public final Integer getMaximumPopulations()
    {
        return maximumPopulations;
    }

    /**
     * Mutator method
     * @param maximumPopulations new value
     */
    public final void setMaximumPopulations(Integer maximumPopulations)
    {
        checkRunning();
        this.maximumPopulations = maximumPopulations;
    }

    /**
     * Accessor method
     * @return Integer containing the property
     */
    public final Integer getPopulationSize()
    {
        return populationSize;
    }

    /**
     * Mutator method
     * @param populationSize new value
     */
    public final void setPopulationSize(Integer populationSize)
    {
        checkRunning();
        this.populationSize = populationSize;
    }

    /**
     * Accessor method (READ-ONLY)
     * @return boolean containing the property
     */
    public final boolean isRunning()
    {
        return running;
    }

    /**
     * Converts the passed individual into a human-readable string using the formatter.
     * @param i Individual to convert
     * @return String containing the human-readable version
     */
    public final String format(Individual i)
    {
        return formatter.format(i);
    }

    /**
     * Converts a collection of individuals into human-readable format
     * @param c Collection to convert
     * @return String containing the human-readable format
     */
    public final String format(Collection < Individual > c)
    {
        return formatter.format(c);
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logSevere(Object o)
    {
        event(new LogEvent(this, o, Level.SEVERE));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logWarning(Object o)
    {
        event(new LogEvent(this, o, Level.WARNING));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logInfo(Object o)
    {
        event(new LogEvent(this, o, Level.INFO));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logConfig(Object o)
    {
        event(new LogEvent(this, o, Level.CONFIG));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logFine(Object o)
    {
        event(new LogEvent(this, o, Level.FINE));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logFiner(Object o)
    {
        event(new LogEvent(this, o, Level.FINER));
    }

    /**
     * Helper function to log issues to all log listeners at the given level
     * @param o Object to log
     */
    public final void logFinest(Object o)
    {
        event(new LogEvent(this, o, Level.FINEST));
    }

    /**
     * Returns whether the passed population is the last one for any of a variety of reasons.
     * @param p Population to test for "lastness" 
     * @return true if this is a last population
     */
    private boolean lastPopulation(Population p)
    {
        return (maximumPopulations != null && p != null && p.getNumber() >= maximumPopulations);
    }

    /**
     * Main method for the lamark instance, which configures and runs the GA.
     * @see java.lang.Runnable#run()
     */
    public final void run()
    {
        startTime = new Long(System.currentTimeMillis());
        try
        {
            if (getConfigurationErrors().size() != 0)
            {
                throw new IllegalStateException("Errors:" + getConfigurationErrors());
            }

            logInfo("top, ab="+aborted);
            
            totalWaitTime=0L;
            // Init the workpackage queue
            WorkPackage.initializeQueue(populationSize);
            
            if (randomSeed!=null)
            {
                random.setSeed(randomSeed);
            }
            logInfo("Started Lamark run at time " + new Date());
            running = true;
            
            // Start a deadlock monitor
            new Thread(new DeadLockMonitor(this,Thread.currentThread())).start();

            // Clear the pre-start queue of events
            firePreStartEvents();
            
            int lowerElitismCount = (int) Math.ceil(populationSize
                * lowerElitism);
            logFine("Lower Elitism Percent=" + lowerElitism + " Size="
                + populationSize);
            logFine("Will replace the lower " + lowerElitismCount
                + " individuals each generation");

            int upperElitismCount = (int) Math.ceil(populationSize
                * upperElitism);
            logFine("Upper Elitism Percent=" + upperElitism + " Size="
                + populationSize);
            logFine("Will reserve the upper " + upperElitismCount
                + " individuals each generation");
            logFine("Will generate "
                + (populationSize - (lowerElitismCount + upperElitismCount))
                + " individuals by crossover each generation");

            Population prev = null;
            
            logInfo("ent ab="+aborted);
            while (!lastPopulation(current) && !aborted && !targetScoreFound())
            {
                // Init next generation
                prev = current;
                current = new Population(this, prev);
         
                int cForceInsert = 0;
                int cUpperElite=0;
                int cLowerElite=0;
                int cCrossover=0;
                                
                // Create the list of work packages
                // Step 1 - Insert any population members in the to-be-inserted
                // list, up to max size
                int remaining = populationSize;
                int toTake = Math.min(remaining, toBeInserted.size());
         
                if (toTake > 0)
                {
                    List < Individual < ? >> insert = new LinkedList<Individual<?>>();
                    for (int i=0;i<toTake;i++)
                    {
                        insert.add(toBeInserted.remove(0));
                    }
                    for (WorkPackage w : WorkPackage.copies(this, current,
                        insert))
                    {
                        submitWorkPackage(w,getCurrentGenerationNumber());
                    }
                    logInfo("Inserted " + toTake
                        + " from the 'tobeinserted' queue");
                    remaining -= toTake;
                    cForceInsert=toTake;
                }
         
                // Step 2 - Insert any upper elitism retained from previous gen
                int upperEliteThisPop = Math.min(upperElitismCount, remaining);
                if (prev != null && upperEliteThisPop > 0)
                {
                    List < Individual < ? >> elite = prev.getIndividuals()
                        .subList(0, upperEliteThisPop);
                    for (WorkPackage w : WorkPackage.copies(this, current,
                        elite))
                    {
                        submitWorkPackage(w,getCurrentGenerationNumber());
                    }
                    remaining -= upperEliteThisPop;
                    cUpperElite = upperEliteThisPop;
                    logInfo("Retained " + upperEliteThisPop
                        + " via upper elitism");
                }
         
                // Step 3 - Replace any via lower elitism
                int lowerEliteThisPop = Math.min(lowerElitismCount, remaining);
                if (lowerEliteThisPop > 0)
                {
                    for (WorkPackage w : WorkPackage.newItems(this, current,
                        lowerEliteThisPop))
                    {
                        submitWorkPackage(w,getCurrentGenerationNumber());
                    }
                    remaining -= lowerEliteThisPop;
                    cLowerElite = lowerEliteThisPop;
                    logInfo("Created " + lowerEliteThisPop
                        + " to replace via lower elitism");
                }
         
                // Step 4 - Create any remnants via crossover (or create if this
                // is the first population)
                if (remaining > 0)
                {
                    logInfo("Creating "+remaining+" via crossover");
                    cCrossover = remaining;
                    if (prev != null)
                    {
                        for (WorkPackage w : WorkPackage.crossovers(this, prev,
                            current, remaining))
                        {
                            submitWorkPackage(w,getCurrentGenerationNumber());
                        }
                        remaining = 0;
                    }
                    else
                    {
                        for (WorkPackage w : WorkPackage.newItems(this,
                            current, remaining))
                        {
                            submitWorkPackage(w,getCurrentGenerationNumber());
                        }
                        remaining = 0;
                    }

                }
                
                event(new PopulationPlanCompleteEvent(this,cForceInsert,cUpperElite,cLowerElite,cCrossover));
                
                // Step 5 - wait on the population to complete
                while (!current.isFilled())
                {
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
                        .fitnessType() == EFitnessType.MINIMUM_BEST))
                {
                    currentBest = current.best();
                    event(new BetterIndividualFoundEvent(this, current,
                            (Individual) current.best()));
                }
                
                // Check if the population is uniform
                if (current !=null && current.isUniform())
                {
                    event(new UniformPopulationEvent(this,current));
                    if (abortOnUniformPopulation)
                    {
                        aborted=true;
                    }
                }
                
            } // End main loop
            logInfo("exit, ab="+aborted);
            
            // If we exited via abortion, notify listeners
            if (aborted)
            {
                if (abortListeners.size() > 0)
                {
                    event(new AbortedEvent(this));
                }
            }
            // Calc exit type and notify listeners
            LastPopulationCompleteEvent.Type exitType = LastPopulationCompleteEvent.Type.BY_POPULATION_NUMBER;
            if (current!=null && current.isUniform())
            {
                exitType = LastPopulationCompleteEvent.Type.UNIFORM;
            }
            else if (targetScoreFound())
            {
                exitType = LastPopulationCompleteEvent.Type.BY_TARGET_SCORE;
            }
            else if (aborted)
            {
                exitType = LastPopulationCompleteEvent.Type.ABORTED;
            }
            
            // Notify listeners of last population
            event(new LastPopulationCompleteEvent(this, current,exitType));
        }
        catch (Throwable t)
        {
            event(new ExceptionEvent(this, t));
            aborted=true;
        }
        finally
        {
            running = false;
            totalRunTime = new Long(System.currentTimeMillis() - startTime);
            startTime = null;
        }
    }
    
    /**
     * Returns the current generation number, or -1 if not started.
     * @return long containing the number
     */
    public long getCurrentGenerationNumber()
    {
        if (current!=null)
        {
            return current.getNumber();
        }
        return -1;
    }
    
    /**
     * Placeholder method for submitting work packages to be processed.
     * @param wp WorkPackage to submit
     * @param genNo Long number of the population this is submitted for
     */
    private void submitWorkPackage(WorkPackage wp,Long genNo)
    {
        // Placeholder in case i want to monitor every time a WP is submitted
        executor.submit(wp);
    }

    
    /**
     * Determine whether we have reached the target score, if any.
     * @return true if we have
     */
    public boolean targetScoreFound()
    {
        if (currentBest!=null && targetScore!=null)
        {
            if (fitnessFunction.fitnessType()==EFitnessType.MAXIMUM_BEST)
            {
                return currentBest.getFitness().compareTo(targetScore)>=0;
            }
            else // min best
            {
                return currentBest.getFitness().compareTo(targetScore)<=0;
            }
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Called by LamarkFactory to pass custom properties into the components of an instance.
     * @param comp Component type to set the value on
     * @param name String containing the property name
     * @param value String containing the property value
     */
    public void setComponentProperty(EComponent comp,String name,String value)
    {
        IConfigurable conf = getComponentForConfig(comp);
        if (conf!=null)
        {
            EConfigResult res = conf.setProperty(name, value);
            event(new ConfigurationEvent(this,conf,name,value,res));
        }
        else
        {
            event(new ConfigurationEvent(this,null,name,value,EConfigResult.MISSING_OR_NOT_CONFIGURABLE));
        }
    }
    
    /** 
     * Finds the appropriate object for configuration if it is set and implements the right interface
     * @param comp Component type to lookup
     * @return IConfigurable object of that type
     */
    private IConfigurable getComponentForConfig(EComponent comp)
    {
        Object o = comp.getComponent(this);
        
        if (o!=null)
        {
            if (IConfigurable.class.isInstance(o))
            {
                return ((IConfigurable)o);
            }
        }
        return null;
    }

    
    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addLogListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            logListeners.add(jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addAbortListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            abortListeners.add(jel);
            logInfo("Added abort listener:"+jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addExceptionListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            exceptionListeners.add(jel);
            logInfo("Added exception listener:"+jel);
        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addBetterIndividualFoundListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            betterIndividualFoundListeners.add(jel);
            logInfo("Added BIF listener:"+jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addLastPopulationCompleteListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            lastPopulationCompleteListeners.add(jel);
            logInfo("Added last pop complete listener:"+jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addUniformPopulationListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            uniformPopulationListeners.add(jel);
            logInfo("Added uniform pop listener:"+jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addPopulationCompleteListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            populationCompleteListeners.add(jel);
            logInfo("Added pop complete listener:"+jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addPopulationPlanCompleteListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            planCompleteListeners.add(jel);
            logInfo("Added pop plan complete listener:"+jel);

        }
    }

    /**
     * Adds a listener for the specified type of events.
     * @param jel LamarkEventListener to add 
     */
    public final void addConfigurationListener(LamarkEventListener jel)
    {
        if (jel!=null)
        {
            configurationListeners.add(jel);
            logInfo("Added conf listener listener:"+jel);

        }
    }
    
    /**
     * Registers this listener to receive ALL events
     * @param jel LamarkEventListener to add 
     */
    public final void addGenericListener(LamarkEventListener jel)
    {
        if (jel != null)
        {
            abortListeners.add(jel);
            betterIndividualFoundListeners.add(jel);
            lastPopulationCompleteListeners.add(jel);
            uniformPopulationListeners.add(jel);
            populationCompleteListeners.add(jel);
            exceptionListeners.add(jel);
            logListeners.add(jel);
            planCompleteListeners.add(jel);
            configurationListeners.add(jel);
            logInfo("Added generic listener:"+jel);
        }
    }

    
    /**
     * Sends the event to all appropriate listeners, or enques it if instance not started.
     * @param event LamarkEvent to broadcast
     * @return true if the event was sent, false if it was enqueud
     */
    public final boolean event(LamarkEvent event)
    {
        if (event!=null)
        {
            if (running)
            {
            Set<LamarkEventListener> listenerSet = null;
            Class tClass = event.getClass();
            if (AbortedEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = abortListeners;
            }
            else if (BetterIndividualFoundEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = betterIndividualFoundListeners;
            }
            else if (ConfigurationEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = configurationListeners;
            }
            else if (ExceptionEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = exceptionListeners;
            }
            else if (LastPopulationCompleteEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = lastPopulationCompleteListeners;
            }
            else if (LogEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = logListeners;
            }
            else if (PopulationCompleteEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = populationCompleteListeners;   
            }
            else if (PopulationPlanCompleteEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = planCompleteListeners;   
            }
            else if (UniformPopulationEvent.class.isAssignableFrom(tClass))
            {
                listenerSet = uniformPopulationListeners;
            }
            
            // Send it to the proper set
            for (LamarkEventListener listener : listenerSet)
            {
                listener.handleEvent(event);
            }
            return true;
            }
            else
            {
                // If not running, just put it in the queue of messages to dump when the processing starts
                // We do this so that it is possible for a user to add their various properties and 
                // components prior to registering their listeners, but at the cost of immediate feedback
                // on configuration events
                preStartEvents.add(event);
                return false;
            }
        }
        return false;
    }
    
    
    /**
     * Emptys the queue of events that occurred prior to startup into the appropriate listeners.
     */
    private void firePreStartEvents()
    {
        if (running)
        {
            for (Iterator<LamarkEvent> i = preStartEvents.iterator();i.hasNext();)
            {
                LamarkEvent next = i.next();
                if (event(next))
                {
                    i.remove();
                }
            }
            if (!preStartEvents.isEmpty())
            {
                throw new IllegalStateException("CANT HAPPEN: failed to empty preStartEvents");
            }
        }
        else
        {
            throw new IllegalStateException("CANT HAPPEN: ClearPreStartEvents should only be called when running.");
        }
    }
    
    /**
     * A function called by subunits (wp's basically) when an exception occurs.
     * Causes the GA to abort and an error message to be thrown and logged.
     * @param e Exception that occured
     */
    public final void exceptionInSubunit(Exception e)
    {
        logSevere("Error occurred:"+e);
        event(new ExceptionEvent(this,e));
        abort();
    }

    /**
     * Sets the abort flag, which causes the GA to stop at the end of the current generation.
     */
    public final void abort()
    {
        this.aborted = true;
    }

    
    /**
     * Returns the time elapsed since instace start in milliseconds.
     * @return long containing the runtime
     */
    public final long currentRuntimeMS()
    {
        if (running && null != startTime)
        {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    /**
     * Returns the estimated time from start to completion milliseconds.
     * NOTE: this function only returns a value if maximumPopulations is
     * set.
     * @return long containing the estimated runtime
     */
    public final long estimatedRuntimeMS()
    {
        if (running && null != startTime && null != current && null != maximumPopulations)
        {
            long curTime = currentRuntimeMS();
            double pctDone = 0;
            if (maximumPopulations != null)
            {
                pctDone = (double) getCurrentGenerationNumber()
                    / (double) maximumPopulations;
            }
            double totalTime = curTime / pctDone;
            return (long) (totalTime - curTime);
        }
        return 0;
    }

    /**
     * Test whether mutation should occur. 
     * @return true if test succeeds, false otherwise.
     */
    public boolean mutationFlip()
    {
        return random.nextDouble() < mutationProbability;
    }

    /**
     * Test whether crossover should occur. 
     * @return true if test succeeds, false otherwise.
     */
    public boolean crossoverFlip()
    {
        return random.nextDouble() < crossoverProbability;
    }
    
    /**
     * Accessor method
     * NOTE: only non-null when the system is running
     * @return Long containing the property
     */
    public final Long getTotalRunTime()
    {
        return totalRunTime;
    }

    /**
     * Accessor method
     * NOTE: only non-null after starting 
     * @return Individual containing the property
     */
    public final Individual getCurrentBest()
    {
        return currentBest;
    }

    /**
     * Accessor method
     * @return ICrossover containing the property
     */
    public final ICrossover getCrossover()
    {
        return crossover;
    }

    /**
     * Accessor method
     * @return IFitnessFunction containing the property
     */
    public final IFitnessFunction getFitnessFunction()
    {
        return fitnessFunction;
    }

    /**
     * Accessor method
     * @return IMutator containing the property
     */
    public final IMutator getMutator()
    {
        return mutator;
    }

    /**
     * Accessor method
     * @return ISelector containing the property
     */
    public final ISelector getSelector()
    {
        return selector;
    }

    /**
     * Accessor method
     * @return ICreator containing the property
     */
    public final ICreator getCreator()
    {
        return creator;
    }

    /**
     * Mutator method
     * @param pCreator new value
     */
    public final void setCreator(ICreator pCreator)
    {
        checkRunning();
        this.creator = pCreator;
        if (creator != null)
        {
            creator.setLamark(this);
        }
    }

    /**
     * Mutator method
     * @param pCrossover new value
     */
    public final void setCrossover(ICrossover pCrossover)
    {
        checkRunning();
        this.crossover = pCrossover;
        if (crossover != null)
        {
            crossover.setLamark(this);
        }
    }

    /**
     * Mutator method
     * @param pFitnessFunction new value
     */
    public final void setFitnessFunction(IFitnessFunction pFitnessFunction)
    {
        checkRunning();
        this.fitnessFunction = pFitnessFunction;
        if (fitnessFunction != null)
        {
            fitnessFunction.setLamark(this);
        }
    }

    /**
     * Mutator method
     * @param pMutator new value
     */
    public final void setMutator(IMutator pMutator)
    {
        checkRunning();
        this.mutator = pMutator;
        if (mutator != null)
        {
            mutator.setLamark(this);
        }
    }

    /**
     * Mutator method
     * @param pSelector new value
     */
    public final void setSelector(ISelector pSelector)
    {
        checkRunning();
        this.selector = pSelector;
        if (selector != null)
        {
            selector.setLamark(this);
        }
    }

    /**
     * Accessor method
     * @return Integer containing the property
     */
    public Integer getNumberOfWorkerThreads()
    {
        return numberOfWorkerThreads;
    }

    /**
     * Mutator method
     * NOTE: If this is less than 1, then an error occurs.  If exactly 1, then a 
     * single-threaded executor is used.  Otherwise, a thread-pool executor is
     * used.
     * @param numberOfWorkerThreads new value
     */
    public void setNumberOfWorkerThreads(Integer numberOfWorkerThreads)
    {
        checkRunning();
        this.numberOfWorkerThreads = numberOfWorkerThreads;
        if (numberOfWorkerThreads < 1)
        {
            throw new IllegalArgumentException(
                "Must have at least 1 worker thread");
        }
        else if (numberOfWorkerThreads == 1)
        {
            this.executor = Executors.newSingleThreadExecutor();
        }
        else
        {
            this.executor = Executors.newFixedThreadPool(numberOfWorkerThreads);
        }
    }

    /**
     * Accessor method
     * @return Long containing the property
     */
    public Long getTotalWaitTime()
    {
        return totalWaitTime;
    }
    
    /**
     * Returns, on average, how much time is spent waiting on work package completion.
     * NOTE: Only non-null after the system is started
     * @return double containing the property
     */
    public Double getAverageWaitTime()
    {
        if (current!=null)
        {
            return totalWaitTime.doubleValue()/(double)getCurrentGenerationNumber();
        }
        return null;
    }

    /**
     * Returns the percentage of total run time that is consumed waiting on work package completion.
     * NOTE: Only non-null when the system is running
     * @return Double containing the percentage
     */
    public Double getPercentageTimeWaiting()
    {
        if (getTotalRunTime()!=null)
        {
            return getTotalWaitTime().doubleValue()/getTotalRunTime().doubleValue();
        }
        return null;
    }
    
    /**
     * Accessor method
     * @return Double containing the property
     */
    public Double getTargetScore()
    {
        return targetScore;
    }

    /**
     * Mutator method
     * @param targetScore new value
     */
    public void setTargetScore(Double targetScore)
    {
        checkRunning();
        this.targetScore = targetScore;
    }

    /**
     * Accessor method
     * @return Long containing the property
     */
    public Long getRandomSeed()
    {
        return randomSeed;
    }

    /**
     * Mutator method
     * @param randomSeed new value
     */
    public void setRandomSeed(Long randomSeed)
    {
        checkRunning();
        this.randomSeed = randomSeed;
    }

    
    
    /**
     * A little inner class to run and make sure the GA hasn't hung up somewhere.
     * 
     * Currently this class does little except send out messages when it thinks
     * a deadlock has occurred.  Probably should be extended to allow forced
     * abortion of the parent threads, as well as configurability on how
     * long to wait before assuming deadlock
     * 
     * @author cweiss
     * @since 10/2007
     */
    class DeadLockMonitor implements Runnable
    {
        
        /** Lamark object to monitor **/
        private Lamark subject;
        /** Last generation number seen when awake **/
        private Long lastGenerationNumber;
        /** Number of times Ive seen that generation when I awoke **/
        private int cycleCount;
        /** Number of times I need to see the same generation before I assume deadlock **/
        private static final int THRESHOLD=4;
        /** Handle to the main thread running lamark **/
        private Thread mainThread;
        
        /**
         * Default constructor
         * @param toMonitor Lamark to monitor for deadlock
         * @param pMainThread Thread to monitor for deadlock
         */
        public DeadLockMonitor(Lamark toMonitor,Thread pMainThread)
        {
            super();
            subject = toMonitor;
            mainThread = pMainThread;
        }
        
        /**
         * Checks the lamark instance to make sure it isnt hung up.
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                cycleCount=0;
                lastGenerationNumber = subject.getCurrentGenerationNumber();
                while (subject!=null && subject.running)
                {
                    if (lastGenerationNumber==subject.getCurrentGenerationNumber())
                    {
                        cycleCount++;
                    }
                    else
                    {
                        lastGenerationNumber=subject.getCurrentGenerationNumber();
                        cycleCount=0;
                        subject.logFinest("Deadlock Check, no DL Found");
                    }
                    if (cycleCount>THRESHOLD)
                    {
                        subject.logWarning("Deadlock detected, gen="+current.getNumber()+" lock = "+" current="+current+" cs="+current.getSize()+" cts="+current.getTargetSize()+" qs:"+WorkPackage.queueSize()+" main state:"+mainThread.getState());
                    }
                    Thread.sleep(1000);
                    
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Accessor method
     * @return boolean containing the property
     */
    public boolean isAbortOnUniformPopulation()
    {
        return abortOnUniformPopulation;
    }

    /**
     * Mutator method
     * @param abortOnUniformPopulation new value for the property
     */
    public void setAbortOnUniformPopulation(boolean abortOnUniformPopulation)
    {
        this.abortOnUniformPopulation = abortOnUniformPopulation;
    }

    /**
     * Accessor method
     * @return Random containing the property
     */
    public Random getRandom()
    {
        return random;
    }

    /**
     * Mutator method
     * @param random new value 
     */
    public void setRandom(Random random)
    {
        checkRunning();
        this.random = random;
    }

    /**
     * Accessor method
     * @return IIndividualFormatter containing the property
     */
    public IIndividualFormatter getFormatter()
    {
        return formatter;
    }

    /**
     * Mutator method
     * @param formatter new value 
     */
    public void setFormatter(IIndividualFormatter formatter)
    {
        this.formatter = formatter;
    }
    
}
