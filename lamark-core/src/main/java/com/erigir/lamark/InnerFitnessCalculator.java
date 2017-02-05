package com.erigir.lamark;

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
        Object toCalculate = (IndividualAwareFitnessFunction.class.isAssignableFrom(calculator.getClass()))?value:value.getGenome();
        double fitness = calculator.applyAsDouble((T)toCalculate);
        value.setFitness(fitness);
        return value;
    }

    ToDoubleFunction<T> getCalculator() {
        return calculator;
    }
}
