package com.erigir.lamark.stream;

import java.util.List;
import java.util.Random;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public interface Selector<T> {
    Individual<T> select(List<Individual<T>> sortedList, Random random, double totalFitness);
}
