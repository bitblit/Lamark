package com.erigir.lamark;


import com.erigir.lamark.events.*;
import com.erigir.lamark.listener.FilteredListener;
import com.erigir.lamark.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class Lamark<T> implements Callable<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Lamark.class);

    // These are state of the run
    private LastPopulationCompleteEvent.Type finishType=null;
    private Individual<T> bestSoFar;
    private Long currentGeneration = 0L;
    private Long started;
    private Long ended;

    // Context is a general holding place for objects that are shared by various
    private Map<String,Object> context = new ConcurrentHashMap<>();

    // Helper functions for wrapping and stripping Individual wrappers from genomes
    private Wrapper<T> wrapper = new Wrapper<>();
    private Stripper<T> stripper = new Stripper<>();

    // Mostly package-level to allow the builder to set them
    List<FilteredListener> listeners = new LinkedList<>();
    List<T> initialValues;
    Random random;
    Integer populationSize;
    Long maxGenerations;
    Supplier<T> supplier;
    InnerFitnessCalculator<T> fitnessFunction;
    InnerCrossover<T> crossover;
    InnerMutator<T> mutator;
    Function<T,String> formatter;
    Selector<T> selector;
    Double targetScore;
    Boolean minimizeScore;
    Integer numberOfParents;


    public void addListener(LamarkEventListener listener)
    {
        Optional<FilteredListener> o = listeners.stream().filter((p)->p.getListener()==listener).findFirst();
        if (!o.isPresent())
        {
            listeners.add(new FilteredListener(listener,null));
        }
    }

    public String format(Individual<T> individual)
    {
        return formatter.apply(individual.getGenome());
    }

    public String format(Collection<Individual<T>> individuals)
    {
        StringBuilder sb = new StringBuilder();
        for (Individual<T> i:individuals)
        {
            sb.append(format(i));
            sb.append(",");
        }
        String out = sb.toString();
        return out.substring(0,out.length()-1);
    }

    public void addListener(LamarkEventListener listener, Set<Class<? extends LamarkEvent>> filter)
    {
        Optional<FilteredListener> o = listeners.stream().filter((p)->p.getListener()==listener).findFirst();
        if (!o.isPresent())
        {
            listeners.add(new FilteredListener(listener,filter));
        }
        else
        {
            o.get().addFilter(filter);
        }
    }

    public void addListener(LamarkEventListener listener, Class<? extends LamarkEvent>... filterTypes )
    {
        addListener(listener, new HashSet<>(Arrays.asList(filterTypes)));
    }

    private void publishEvent(LamarkEvent event)
    {
        for (FilteredListener f:listeners)
        {
            f.applyEvent(event);
        }
    }

    private void updateIfShouldKeepRunning(List<Individual<T>> currentPopulation)
    {
        if (maxGenerations!=null && currentGeneration>=maxGenerations)
        {
            finishType = LastPopulationCompleteEvent.Type.BY_POPULATION_NUMBER;
        }
        else if (targetScore!=null && bestSoFar!=null && bestSoFar.getFitness().compareTo(targetScore)>=0)
        {
            finishType = LastPopulationCompleteEvent.Type.BY_TARGET_SCORE;
        }
        else
        {
            boolean allEqual = true;
            T first = currentPopulation.get(0).getGenome();
            for (int i=1;i<currentPopulation.size() && allEqual;i++)
            {
                allEqual = currentPopulation.get(i).getGenome().equals(first);
            }
            if (allEqual)
            {
                publishEvent(new UniformPopulationEvent(this, currentPopulation, currentGeneration));
                finishType = LastPopulationCompleteEvent.Type.UNIFORM;
            }
        }
    }

    public void stop()
    {
        LOG.warn("Aborted!");
        finishType = LastPopulationCompleteEvent.Type.ABORTED;
        publishEvent(new AbortedEvent(this));
    }

    public void start()
    {
        LOG.info("Running Lamark in calling thread");
        call();
    }

    /**
     * For any component that implements context awareness, give them a handle
     */
    private void addContextReferences()
    {
        for (Object o:Arrays.asList(supplier, fitnessFunction.getCalculator(), crossover.getCrossover(), mutator.getMutator(),formatter,selector))
        {
            if (ContextAware.class.isAssignableFrom(o.getClass()))
            {
                LOG.info("ContextAware : {}",o);
                ((ContextAware)o).setContext(context);
            }
        }
    }

    public T call()
    {
        LOG.info("About to start the Lamark process");
        addContextReferences();
        try {
            started = System.currentTimeMillis();

            // create generation
            List<T> items = new ArrayList<>(populationSize);
            for (int i = 0; i < populationSize; i++) {
                items.add(supplier.get());
            }

            LOG.debug("Items on startup: {}", items);
            List<Individual<T>> curGen = null;

            try {
                curGen = items.stream().map(wrapper).collect(Collectors.toList());
            } catch (Exception e) {
                publishEvent(new ExceptionEvent(this, e));
                this.stop();
            }

            while (finishType == null) {
                try {
                    // Start each generation with a list of individuals with no fitness value yet

                    // Calc the fit values and sort
                    curGen = curGen.stream().map(fitnessFunction).sorted().collect(Collectors.toList());

                    // If we've found a new best, update and report
                    if (minimizeScore) {
                        if (bestSoFar == null || curGen.get(curGen.size() - 1).getFitness().compareTo(bestSoFar.getFitness()) < 0) {
                            bestSoFar = curGen.get(curGen.size() - 1);
                            publishEvent(new BetterIndividualFoundEvent(this, curGen, currentGeneration, bestSoFar));
                        }
                    } else {
                        if (bestSoFar == null || curGen.get(0).getFitness().compareTo(bestSoFar.getFitness()) > 0) {
                            bestSoFar = curGen.get(0);
                            publishEvent(new BetterIndividualFoundEvent(this, curGen, currentGeneration, bestSoFar));
                        }
                    }

                    // Calc total
                    LOG.debug("Generation {}:{}", currentGeneration, curGen);
                    LOG.debug("Stripped: {}", curGen.stream().map(stripper).collect(Collectors.toList()));

                    double totalFitness = curGen.stream().mapToDouble((p) -> p.getFitness()).sum();
                    LOG.debug("Total fitness: {}", totalFitness);

                    // Select for crossover (select a bunch at once, and then create parent lists from them
                    // Designed this way because the selectors typically do a bunch of calcs that can be reused
                    // over and over
                    List<Individual<T>> selected = selector.select(curGen, random, numberOfParents * populationSize, minimizeScore);
                    // Stats - increment all of their selected counters
                    selected.stream().forEach((p) -> p.incrementSelected());

                    List<List<Individual<T>>> parents = new ArrayList<>(populationSize);
                    for (int i = 0; i < populationSize; i++) {
                        parents.add(selected.subList(i * numberOfParents, (i + 1) * numberOfParents));
                    }
                    publishEvent(new PopulationCompleteEvent(this, curGen, currentGeneration));

                    // At the end of each cycle check exit conditions
                    updateIfShouldKeepRunning(curGen);
                    // --------------------
                    // Next generation starts here
                    // TODO: Upper/lower elitism

                    currentGeneration++;
                    // Apply crossover and mutation
                    curGen = parents.stream().map(crossover).map(mutator).collect(Collectors.toList());
                } catch (Exception e) {
                    LOG.warn("Error", e);
                    publishEvent(new ExceptionEvent(this, e));
                }

            }

            publishEvent(new LastPopulationCompleteEvent<>(this, curGen, currentGeneration, finishType));
            ended = System.currentTimeMillis();

            return (bestSoFar == null) ? null : bestSoFar.getGenome();
        }
        catch (Exception e)
        {
            LOG.error("Main error capture in lamark class",e);
            return null;
        }
    }

    public Long getRunTime()
    {
        Long rval = null;
        if (started!=null)
        {
            rval = (ended==null)?System.currentTimeMillis()-started:ended-started;
        }
        return rval;
    }

    public Long getEstimatedRunTime()
    {
        Long rval = null;
        if (started!=null && maxGenerations!=null)
        {
            if (ended==null)
            {
                double pctDone = (double)currentGeneration/(double)maxGenerations;
                rval = (long)((double)getRunTime()/pctDone);
            }
            else
            {
                rval = ended;
            }
        }
        return rval;
    }

    public boolean isRunning()
    {
        return (started!=null && ended==null);
    }

    public Individual getBestSoFar() {
        return bestSoFar;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
