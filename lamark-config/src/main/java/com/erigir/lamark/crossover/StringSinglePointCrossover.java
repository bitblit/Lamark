package com.erigir.lamark.crossover;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class StringSinglePointCrossover extends AbstractLamarkComponent implements Function<List<String>,String> {

    public StringSinglePointCrossover(Random random) {
        super(random);
    }

    public StringSinglePointCrossover() {
        super();
    }

    @Override
    public String apply(List<String> strings) {
        Objects.requireNonNull(strings);
        if (strings.size()!=2)
        {
            throw new IllegalArgumentException("Expected 2 args");
        }
        if (strings.get(0).length()!=strings.get(1).length())
        {
            throw new IllegalArgumentException("Strings must be same lenght");
        }

        int split = rand().nextInt(strings.get(0).length());
        StringBuilder sb = new StringBuilder();
        sb.append(strings.get(0).substring(0,split));
        sb.append(strings.get(1).substring(split));
        return sb.toString();
    }
}
