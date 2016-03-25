package com.erigir.lamark.stream;


import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class TestStreamLamark<T> {

    public static void main(String[] args) {
        try
        {
            final Random random = new Random();
            final int size = 10;
            final long maxGenerations = 50;

            Supplier<String> creator = new Supplier<String>() {
                @Override
                public String get() {
                    StringBuilder sb = new StringBuilder();
                    for (int i=0;i<size;i++)
                    {
                        sb.append((random.nextBoolean())?"1":"0");
                    }
                    return sb.toString();
                }
            };
            ToDoubleFunction<String> fitnessFunction = new ToDoubleFunction<String>() {
                @Override
                public double applyAsDouble(String s) {
                    int count = 0;
                    for (char c:s.toCharArray())
                    {
                        if (c=='1')
                        {
                            count++;
                        }
                    }
                    return (double)count/(double)s.length();
                }
            };
            Function<List<String>, String> crossover = new Function<List<String>, String>() {
                @Override
                public String apply(List<String> strings) {
                    int split = random.nextInt(size);
                    StringBuilder sb = new StringBuilder();
                    sb.append(strings.get(0).substring(0,split));
                    sb.append(strings.get(1).substring(split));
                    return sb.toString();
                }
            };
            Function<String, String> mutator = new Function<String, String>() {
                @Override
                public String apply(String s) {
                    int loc = random.nextInt(s.length());
                    StringBuilder sb = new StringBuilder();

                    for (int i=0;i<s.length();i++)
                    {
                        if (i==loc)
                        {
                            sb.append((random.nextBoolean())?"1":"0");
                        }
                        else
                        {
                            sb.append(s.charAt(i));
                        }
                    }
                    return sb.toString();
                }
            };

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

            lamark.process();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
