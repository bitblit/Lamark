package com.erigir.lamark;


import com.erigir.lamark.events.*;
import com.erigir.lamark.selector.RouletteWheelSelector;
import com.erigir.lamark.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class Lamark<T> implements Callable<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Lamark.class);

    private List<FilteredListener> listeners = new LinkedList<>();

    private LastPopulationCompleteEvent.Type finishType=null;
    private Individual<T> bestSoFar;
    private List<T> initialValues;
    private Random random;
    private Integer populationSize;
    private Long maxGenerations;
    private Long currentGeneration = 0l;
    private Supplier<T> creator;
    //private ToDoubleFunction<T> fitnessFunction;
    private InnerFitnessCalculator<T> fitnessFunction;
    private InnerCrossover<T> crossover;
    private InnerMutator<T> mutator;
    private Function<T,String> formatter = new Function<T, String>() {
        @Override
        public String apply(T t) {
            return String.valueOf(t);
        }
    };
    //private Function<List<T>, T> crossover;
    //private Function<T,T> mutator;
    private Wrapper<T> wrapper = new Wrapper<>();
    private Stripper<T> stripper = new Stripper<>();
    private Selector<T> selector = new RouletteWheelSelector<>();
    private Double targetScore = null;
    private boolean minimizeScore = false;
    private int numberOfParents = 2;

    private Long started;
    private Long ended;

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


    public T call()
    {
        started = System.currentTimeMillis();

        // create generation
        List<T> items = new ArrayList<>(populationSize);
        for (int i=0;i<populationSize;i++)
        {
            items.add(creator.get());
        }

        LOG.debug("Items on startup: {}",items);
        List<Individual<T>> curGen = null;

        try {
            curGen = items.stream().map(wrapper).collect(Collectors.toList());
        }
        catch (Exception e)
        {
            publishEvent(new ExceptionEvent(this, e));
            this.stop();
        }

        while (finishType == null)
        {
            try {
                // Start each generation with a list of individuals with no fitness value yet

                // Calc the fit values and sort
                curGen = curGen.stream().map(fitnessFunction).sorted().collect(Collectors.toList());

                // If we've found a new best, update and report
                if (minimizeScore)
                {
                    if (bestSoFar == null || curGen.get(curGen.size()-1).getFitness().compareTo(bestSoFar.getFitness()) < 0)
                    {
                        bestSoFar = curGen.get(curGen.size()-1);
                        publishEvent(new BetterIndividualFoundEvent(this, curGen, currentGeneration , bestSoFar));
                    }
                }
                else
                {
                    if (bestSoFar == null || curGen.get(0).getFitness().compareTo(bestSoFar.getFitness()) > 0)
                    {
                        bestSoFar = curGen.get(0);
                        publishEvent(new BetterIndividualFoundEvent(this, curGen, currentGeneration , bestSoFar));
                    }
                }

                // Calc total
                LOG.debug("Generation {}:{}" , currentGeneration , curGen);
                LOG.debug("Stripped: {}" , curGen.stream().map(stripper).collect(Collectors.toList()));

                double totalFitness = curGen.stream().mapToDouble((p) -> p.getFitness()).sum();
                LOG.debug("Total fitness: {}" , totalFitness);

                // Select for crossover (select a bunch at once, and then create parent lists from them
                // Designed this way because the selectors typically do a bunch of calcs that can be reused
                // over and over
                List<Individual<T>> selected = selector.select(curGen, random, numberOfParents*populationSize, minimizeScore);
                // Stats - increment all of their selected counters
                selected.stream().forEach((p)->p.incrementSelected());

                List<List<Individual<T>>> parents = new ArrayList<>(populationSize);
                for (int i = 0;i<populationSize;i++)
                {
                    parents.add(selected.subList(i*numberOfParents, (i+1)*numberOfParents));
                }
                publishEvent(new PopulationCompleteEvent(this, curGen,currentGeneration));

                // At the end of each cycle check exit conditions
                updateIfShouldKeepRunning(curGen);
                // --------------------
                // Next generation starts here
                // TODO: Upper/lower elitism

                currentGeneration++;
                // Apply crossover and mutation
                curGen = parents.stream().map(crossover).map(mutator).collect(Collectors.toList());
            }
            catch (Exception e)
            {
                LOG.warn("Error",e);
                publishEvent(new ExceptionEvent(this, e));
            }

        }

        publishEvent(new LastPopulationCompleteEvent<>(this, curGen, currentGeneration, finishType ));
        ended = System.currentTimeMillis();

        return (bestSoFar==null)?null:bestSoFar.getGenome();
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


    public Individual getBestSoFar() {
        return bestSoFar;
    }

    public static class LamarkBuilder<T>
    {
        private Random random;
        private Long maxGenerations;
        private Supplier<T> creator;
        private List<T> initialValues;
        private ToDoubleFunction<T> fitnessFunction;
        private Function<List<T>,T> crossover;
        private Function<T,T> mutator;
        private Function<T,String> formatter;
        private double pCrossover=1.0;
        private double pMutation=.005;
        private Selector selector;
        private Integer populationSize;

        // TODO: Implement the stuff below
        private Double upperElitism;
        private Double lowerElitism;
        private Double targetScore;

        private boolean trackParentage;
        private boolean abortOnUniformPopulation;
        private boolean runParallel;
        private boolean minimizeScore;


        public Lamark<T> build()
        {
            Objects.requireNonNull(creator);
            Objects.requireNonNull(fitnessFunction);
            Objects.requireNonNull(crossover);
            Objects.requireNonNull(mutator);
            Objects.requireNonNull(selector);
            Objects.requireNonNull(populationSize);

            if (pCrossover>1.0 || pMutation>1.0 || pCrossover<0 || pMutation<0)
            {
                throw new IllegalArgumentException("Probabilities must be between 0 and 1, inclusive");
            }

            Lamark<T> rval = new Lamark<>();
            rval.random = (random==null)?new Random():random;
            rval.maxGenerations = maxGenerations;
            rval.creator = creator;
            rval.crossover = new InnerCrossover<>(crossover, pCrossover, rval.random);
            rval.mutator = new InnerMutator<>(mutator, pMutation, rval.random);
            rval.initialValues = initialValues;
            rval.fitnessFunction = new InnerFitnessCalculator<>(fitnessFunction);
            rval.selector = (selector==null)?new RouletteWheelSelector<>():selector;
            rval.populationSize = populationSize;
            rval.minimizeScore = minimizeScore;

            if (formatter!=null)
            {
                rval.formatter = formatter;
            }

            return rval;
        }

        public LamarkBuilder withRandom(final Random random) {
            this.random = random;
            return this;
        }


        public LamarkBuilder withMaxGenerations(final Long maxGenerations) {
            this.maxGenerations = maxGenerations;
            return this;
        }

        public LamarkBuilder withCreator(final Supplier<T> creator) {
            this.creator = creator;
            return this;
        }

        public LamarkBuilder withInitialValues(final List<T> initialValues) {
            this.initialValues = initialValues;
            return this;
        }

        public LamarkBuilder withFitnessFunction(final ToDoubleFunction<T> fitnessFunction) {
            this.fitnessFunction = fitnessFunction;
            return this;
        }

        public LamarkBuilder withCrossover(final Function<List<T>, T> crossover) {
            this.crossover = crossover;
            return this;
        }

        public LamarkBuilder withMutator(final Function<T, T> mutator) {
            this.mutator = mutator;
            return this;
        }

        public LamarkBuilder withSelector(final Selector selector) {
            this.selector = selector;
            return this;
        }

        public LamarkBuilder withPMutation(final double pMutation) {
            this.pMutation = pMutation;
            return this;
        }

        public LamarkBuilder withPCrossover(final double pCrossover) {
            this.pCrossover = pCrossover;
            return this;
        }

        public LamarkBuilder withPopulationSize(final Integer populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public LamarkBuilder withFormatter(final Function<T, String> formatter) {
            this.formatter = formatter;
            return this;
        }

        public LamarkBuilder withMinimizeScore(final boolean minimizeScore) {
            this.minimizeScore = minimizeScore;
            return this;
        }

        public LamarkBuilder withUpperElitism(final Double upperElitism) {
            this.upperElitism = upperElitism;
            return this;
        }

        public LamarkBuilder withLowerElitism(final Double lowerElitism) {
            this.lowerElitism = lowerElitism;
            return this;
        }

        public LamarkBuilder withTargetScore(final Double targetScore) {
            this.targetScore = targetScore;
            return this;
        }


    }


}
