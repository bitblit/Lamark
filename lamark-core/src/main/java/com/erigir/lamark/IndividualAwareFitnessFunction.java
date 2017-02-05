package com.erigir.lamark;

import java.util.function.ToDoubleFunction;

/**
 * Flag interface marks a fitness function as wanting to consume the Individual
 * object instead of the wrapped Genome - usually used for if you need to cache
 * things in the Individual's attributes section
 * Created by cweiss1271 on 2/4/17.
 */
public interface IndividualAwareFitnessFunction extends ToDoubleFunction<Individual>{
}
