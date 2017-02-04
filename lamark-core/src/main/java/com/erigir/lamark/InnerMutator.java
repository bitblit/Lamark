package com.erigir.lamark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.function.Function;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class InnerMutator<T> implements Function<Individual<T>, Individual<T>>
{
    private static final Logger LOG = LoggerFactory.getLogger(InnerMutator.class);

    private Function<T, T> mutator;
    private Random random;
    private double pMutation;

    public InnerMutator(Function<T, T> mutator, double pMutation, Random random) {
        this.mutator = mutator;
        this.random = random;
        this.pMutation = pMutation;
    }

    @Override
    public Individual<T> apply(Individual<T> value) {
        if (random.nextDouble()<=pMutation)
        {
            LOG.trace("Mutation occurred on {}",value);
            value.setGenome(mutator.apply(value.getGenome()));
            value.setMutated(true);
        }
        return value;
    }

    Function<T, T> getMutator() {
        return mutator;
    }
}
