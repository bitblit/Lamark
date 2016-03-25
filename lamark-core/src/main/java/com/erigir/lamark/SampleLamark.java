package com.erigir.lamark;

import java.util.List;

/**
 * Created by cweiss1271 on 11/18/15.
 */
public class SampleLamark {

    @Crossover (id="mycrossover")
    public String myCrossover(@Parent String parent1, @Parent String parent2, @LamarkParam("currentGeneration")int generation)
    {
        return null;
    }

    @Selector (id="myselect")
    public String mySelect(List<ScoredGenome<String>> generation)
    {

    }

    ScoredGenome<T> implements Comparable
    T GetGenome
    Double getScore
    MinMax type

    @Selector(id=...)
    T select(List<ScoredGenome<T>> generation)

    @FitnessFunction (id=..., type=MinMax)
    Double score(T input)

    @Mutator(id=...)
    T mutatexxx(@MutateSource T src)

    @Creator(id=...)
    T createxxx(@LamarkParam(name)int value)

    @ParamProvider??

    LamarkConfigurator (LamarkConfig setupoverrides, Set<class> introspect, List<class loader> class loader) returns Lamark

    If only one of each, uses those
    Possible to create a Lamark with nothing but set of classes (default config or provided by annotated method, default class path) config overrides defaults or methods

    Can create "helper class" with one of each that just delegates, make it the only class in the scan

    @LamarkListener(type=abortedevent)
    Void hear( abortedevent evt)
}
