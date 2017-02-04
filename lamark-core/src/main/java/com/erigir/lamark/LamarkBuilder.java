package com.erigir.lamark;

import com.erigir.lamark.selector.RouletteWheelSelector;
import com.erigir.lamark.selector.Selector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Created by cweiss1271 on 3/25/16.
 */
public class LamarkBuilder<T> {
    private Random random;

    private Selector selector;
    private Supplier<T> supplier;
    private ToDoubleFunction<T> fitnessFunction;
    private Function<List<T>,T> crossover;
    private Function<T,T> mutator;
    private Function<T,String> formatter;

    private double crossoverProbability =1.0;
    private double mutationProbability =.005;
    private Integer populationSize;
    private Integer numberOfParents=2;
    private Long maxGenerations;

    private List<T> initialValues;

    // TODO: Implement the stuff below
    private Double upperElitismPercentage;
    private Double lowerElitismPercentage;
    private Double targetScore;

    private boolean trackParentage;
    private boolean abortOnUniformPopulation;
    private boolean runParallel;
    private boolean minimizeScore;


    public Lamark<T> build()
    {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(fitnessFunction);
        Objects.requireNonNull(crossover);
        Objects.requireNonNull(mutator);
        Objects.requireNonNull(selector);
        Objects.requireNonNull(populationSize);

        if (crossoverProbability >1.0 || mutationProbability >1.0 || crossoverProbability <0 || mutationProbability <0)
        {
            throw new IllegalArgumentException("Probabilities must be between 0 and 1, inclusive");
        }

        Lamark<T> rval = new Lamark<>();
        rval.random = (random==null)?new Random():random;
        rval.maxGenerations = maxGenerations;
        rval.supplier = supplier;
        rval.crossover = new InnerCrossover<>(crossover, crossoverProbability, rval.random);
        rval.mutator = new InnerMutator<>(mutator, mutationProbability, rval.random);
        rval.initialValues = initialValues;
        rval.fitnessFunction = new InnerFitnessCalculator<>(fitnessFunction);
        rval.selector = (selector==null)?new RouletteWheelSelector<>():selector;
        rval.populationSize = populationSize;
        rval.minimizeScore = minimizeScore;
        rval.formatter = (formatter==null)?new DefaultFormatter<>():formatter;
        rval.targetScore = targetScore;
        rval.minimizeScore = minimizeScore;
        rval.numberOfParents = numberOfParents;

        // Perform any self vai
        for (Object o: Arrays.asList(supplier, fitnessFunction, crossover,mutator, rval.selector))
        {
            if (SelfValidating.class.isAssignableFrom(o.getClass()))
            {
                ((SelfValidating)o).selfValidate();
            }
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

    public LamarkBuilder withSupplier(final Supplier<T> supplier) {
        this.supplier = supplier;
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

    public LamarkBuilder withCrossoverProbability(final double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
        return this;
    }

    public LamarkBuilder withMutationProbability(final double mutationProbability) {
        this.mutationProbability = mutationProbability;
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
        this.upperElitismPercentage = upperElitism;
        return this;
    }

    public LamarkBuilder withLowerElitism(final Double lowerElitism) {
        this.lowerElitismPercentage = lowerElitism;
        return this;
    }

    public LamarkBuilder withTargetScore(final Double targetScore) {
        this.targetScore = targetScore;
        return this;
    }

    public LamarkBuilder withNumberOfParents(final Integer numberOfParents) {
        this.numberOfParents = numberOfParents;
        return this;
    }

    public LamarkBuilder withTrackParentage(final boolean trackParentage) {
        this.trackParentage = trackParentage;
        return this;
    }

    public LamarkBuilder withAbortOnUniformPopulation(final boolean abortOnUniformPopulation) {
        this.abortOnUniformPopulation = abortOnUniformPopulation;
        return this;
    }

    public LamarkBuilder withRunParallel(final boolean runParallel) {
        this.runParallel = runParallel;
        return this;
    }

    public Random getRandom() {
        return random;
    }

    public Long getMaxGenerations() {
        return maxGenerations;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }

    public List<T> getInitialValues() {
        return initialValues;
    }

    public ToDoubleFunction<T> getFitnessFunction() {
        return fitnessFunction;
    }

    public Function<List<T>, T> getCrossover() {
        return crossover;
    }

    public Function<T, T> getMutator() {
        return mutator;
    }

    public Function<T, String> getFormatter() {
        return formatter;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public Selector getSelector() {
        return selector;
    }

    public Integer getPopulationSize() {
        return populationSize;
    }

    public Integer getNumberOfParents() {
        return numberOfParents;
    }

    public Double getUpperElitismPercentage() {
        return upperElitismPercentage;
    }

    public Double getLowerElitismPercentage() {
        return lowerElitismPercentage;
    }

    public Double getTargetScore() {
        return targetScore;
    }

    public boolean isTrackParentage() {
        return trackParentage;
    }

    public boolean isAbortOnUniformPopulation() {
        return abortOnUniformPopulation;
    }

    public boolean isRunParallel() {
        return runParallel;
    }

    public boolean isMinimizeScore() {
        return minimizeScore;
    }


}
