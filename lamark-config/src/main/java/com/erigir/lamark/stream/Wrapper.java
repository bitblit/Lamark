package com.erigir.lamark.stream;

import java.util.ArrayList;
import java.util.List;
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
