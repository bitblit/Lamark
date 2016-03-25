package com.erigir.lamark.stream;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class InnerFitnessCalculator<T> implements Function<Individual<T>, Individual<T>>
{
    private ToDoubleFunction<T> calculator;

    public InnerFitnessCalculator(ToDoubleFunction<T> calculator) {
        this.calculator = calculator;
    }

    @Override
    public Individual<T> apply(Individual<T> value) {
        double fitness = calculator.applyAsDouble(value.getGenome());
        value.setFitness(fitness);
        return value;
    }
}
