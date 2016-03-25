package com.erigir.lamark.stream;


import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class StreamLamark<T> {

    private List<T> initialValues;
    private Random random;
    private Integer populationSize;
    private Long maxGenerations;
    private int currentGeneration = 0;
    private Supplier<T> creator;
    //private ToDoubleFunction<T> fitnessFunction;
    private InnerFitnessCalculator<T> fitnessFunction;
    private InnerCrossover<T> crossover;
    private InnerMutator<T> mutator;
    //private Function<List<T>, T> crossover;
    //private Function<T,T> mutator;
    private Wrapper<T> wrapper = new Wrapper<>();
    private Stripper<T> stripper = new Stripper<>();
    private Selector<T> selector = new RouletteWheelSelector<>();

    private boolean aborted = false;

    private boolean shouldKeepRunning()
    {
        return (!aborted && (maxGenerations==null || currentGeneration<maxGenerations));
    }

    public void stop()
    {
        System.out.println("Aborted!");
        aborted=true;
    }

    public void start()
    {
        // create generation
        List<T> items = new ArrayList<>(populationSize);
        for (int i=0;i<populationSize;i++)
        {
            items.add(creator.get());
        }

        System.out.println("Got these items : "+items);

        List<Individual<T>> curGen = items.stream().map(wrapper).collect(Collectors.toList());

        while (shouldKeepRunning())
        {
            // Start each generation with a list of individuals with no fitness value yet

            // Calc the fit values and sort
            curGen = curGen.stream().map(fitnessFunction).sorted().collect(Collectors.toList());
            // Calc total
            System.out.println("Generation "+currentGeneration+" : "+curGen);
            System.out.println("Stripped:"+curGen.stream().map(stripper).collect(Collectors.toList()));

            double totalFitness = curGen.stream().mapToDouble((p)->p.getFitness()).sum();
            // TODO: impl selector
            System.out.println("Total fitness: "+totalFitness);

            // Select for crossover
            List<List<Individual<T>>> parents = new ArrayList<>();
            for (int i=0;i<populationSize;i++)
            {
                parents.add(Arrays.asList(selector.select(curGen, random, totalFitness), selector.select(curGen,random, totalFitness)));
            }

            System.out.println("Generating next gen...");
            currentGeneration++;
            // Apply crossover and mutation
            curGen = parents.stream().map(crossover).map(mutator).collect(Collectors.toList());
        }

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
        private double pCrossover=1.0;
        private double pMutation=.005;
        private Selector selector;
        private Integer populationSize;

        private Double upperElitism;
        private Double lowerElitism;
        private Double targetScore;

        private boolean trackParentage;
        private boolean abortOnUniformPopulation;


        public StreamLamark<T> build()
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

            StreamLamark<T> rval = new StreamLamark<>();
            rval.random = (random==null)?new Random():random;
            rval.maxGenerations = maxGenerations;
            rval.creator = creator;
            rval.crossover = new InnerCrossover<>(crossover, pCrossover, random);
            rval.mutator = new InnerMutator<>(mutator, pMutation, random);
            rval.initialValues = initialValues;
            rval.fitnessFunction = new InnerFitnessCalculator<>(fitnessFunction);
            rval.selector = (selector==null)?new RouletteWheelSelector<>():selector;
            rval.populationSize = populationSize;
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


    }


}
