package com.erigir.lamark.selector;

import com.erigir.lamark.Individual;

import java.util.List;
import java.util.Random;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public interface Selector<T> {
    List<Individual<T>> select(List<Individual<T>> sortedList, Random random, int numberToSelect, boolean minimumBest);
}
