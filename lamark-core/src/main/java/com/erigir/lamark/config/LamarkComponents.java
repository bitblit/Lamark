package com.erigir.lamark.config;

import com.erigir.lamark.DefaultFormatter;
import com.erigir.lamark.LamarkBuilder;
import com.erigir.lamark.selector.RouletteWheelSelector;
import com.erigir.lamark.selector.Selector;
import lombok.Data;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkComponents {
    private LamarkComponentDetails supplier;
    private LamarkComponentDetails crossover;
    private LamarkComponentDetails fitnessFunction;
    private LamarkComponentDetails mutator;
    private LamarkComponentDetails selector = LamarkComponentDetails.createSingle(RouletteWheelSelector.class, null);
    private LamarkComponentDetails formatter = LamarkComponentDetails.createSingle(DefaultFormatter.class, null);

    public LamarkBuilder applyToBuilder(LamarkBuilder builder)
    {
        builder.withSupplier((Supplier)supplier.createConfiguredObject());
        builder.withCrossover((Function)crossover.createConfiguredObject());
        builder.withFitnessFunction((ToDoubleFunction) fitnessFunction.createConfiguredObject());
        builder.withMutator((Function)mutator.createConfiguredObject());
        builder.withSelector((Selector) selector.createConfiguredObject());
        builder.withFormatter((Function)formatter.createConfiguredObject());
        return builder;
    }
}
