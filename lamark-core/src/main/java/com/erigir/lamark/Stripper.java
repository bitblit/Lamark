package com.erigir.lamark;

import java.util.function.Function;

/**
 * Given an individual object, strip it down to just the genome
 * Created by cweiss1271 on 3/24/16.
 */
public class Stripper<T> implements Function<Individual<T>, T>
{
    @Override
    public T apply(Individual<T> value) {
        return value.getGenome();
    }
}
