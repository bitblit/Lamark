package com.erigir.lamark;

import java.util.function.Function;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class Wrapper<T> implements Function<T, Individual<T>>
{
    @Override
    public Individual<T> apply(T value) {
        return new Individual<>(value);
    }
}
