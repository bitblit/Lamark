package com.erigir.lamark.mutator;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class StringSimpleMutator extends AbstractLamarkComponent implements Function<String,String> {

    public StringSimpleMutator(Random random) {
        super(random);
    }

    public StringSimpleMutator() {
        super();
    }

    @Override
    public String apply(String input) {
        Objects.requireNonNull(input);

        int point1 = rand().nextInt(input.length());
        int point2 = rand().nextInt(input.length());

        if (point2==point1)
        {
            point2 = ((point2+1)%input.length());
        }

        char[] data = input.toCharArray();
        char temp = data[point1];
        data[point1]=data[point2];
        data[point2]=temp;
        return new String(data);
    }
}
