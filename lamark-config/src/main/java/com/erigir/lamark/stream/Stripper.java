package com.erigir.lamark.stream;

import java.util.function.Function;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class Stripper<T> implements Function<Individual<T>, T>
{
    @Override
    public T apply(Individual<T> value) {
        return value.getGenome();
    }
}
