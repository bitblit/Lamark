package com.erigir.lamark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * A wrapper around the crossover function to isolate out the use of the individual object -
 * basically allows the GA writer to not be concerning with stats tracking over time
 *
 * Created by cweiss1271 on 3/24/16.
 */
public class InnerCrossover<T> implements Function<List<Individual<T>>, Individual<T>>
{
    private Function<List<T>, T> crossover;
    private Random random;
    private double pCrossover;

    public InnerCrossover(Function<List<T>, T> crossover, double pCrossover, Random random) {
        this.crossover = crossover;
        this.random = random;
        this.pCrossover = pCrossover;
    }

    @Override
    public Individual<T> apply(List<Individual<T>> value) {
        Individual<T> rval = null;
        if (random.nextDouble()<=pCrossover)
        {
            List<T> temp = new ArrayList<>(value.size());
            for (Individual<T> i:value)
            {
                temp.add(i.getGenome());
            }
            T output = this.crossover.apply(temp);
            rval = new Individual<>(output);
            // track parentage here?
        }
        else
        {
            rval = new Individual<>(value.get(0).getGenome());
        }
        return rval;

    }

    public Function<List<T>, T> getCrossover() {
        return crossover;
    }
}
