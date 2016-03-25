package com.erigir.lamark.stream;


import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.creator.StringCreator;
import com.erigir.lamark.fitness.StringFinderFitness;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.crossover.StringSinglePointCrossover;
import com.erigir.lamark.selector.RouletteWheelSelector;
import com.erigir.lamark.selector.Selector;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class TestStreamLamark<T> implements LamarkEventListener{

    public static void main(String[] args) {
        try
        {
            new TestStreamLamark<>().run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
            throws Exception
    {
        final Random random = new Random();
        final int size = 10;
        final long maxGenerations = 50;

        Supplier<String> creator = StringCreator.alphaCreator(6);
        ToDoubleFunction<String> fitnessFunction = new StringFinderFitness("LAMARK");
        Function<String,String> mutator = new StringSimpleMutator();
        Function<List<String>,String> crossover = new StringSinglePointCrossover();
        Selector<String> selector = new RouletteWheelSelector<>();

        StreamLamark lamark = new StreamLamark.LamarkBuilder<String>()
                .withCreator(creator)
                .withCrossover(crossover)
                .withFitnessFunction(fitnessFunction)
                .withInitialValues(null)
                .withMaxGenerations(maxGenerations)
                .withMutator(mutator)
                .withRandom(random)
                .withSelector(selector)
                .withPCrossover(1.0)
                .withPMutation(.005)
                .build();

        lamark.addListener(this);
        lamark.start();
    }

    @Override
    public void handleEvent(LamarkEvent je) {
        System.out.println("Got event : "+je);
    }
}
