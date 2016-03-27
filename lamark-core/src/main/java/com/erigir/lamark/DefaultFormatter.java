package com.erigir.lamark;

import java.util.function.Function;

/**
 * The default way to convert objects into strings
 * Created by cweiss1271 on 3/25/16.
 */
public class DefaultFormatter<T> implements Function<T,String> {
    @Override
    public String apply(T t) {
        return String.valueOf(t);
    }
}
