package com.erigir.lamark.stream;

import java.util.List;
import java.util.Random;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class RouletteWheelSelector<T> implements Selector<T> {
    public Individual<T> select(List<Individual<T>> sortedList, Random random, double totalFitness)
    {
        double rVal = random.nextDouble()*totalFitness;

        double adder = 0;
        int idx=0;
        while (adder<rVal)
        {
            adder+=sortedList.get(idx).getFitness();
            idx++;
        }
        return sortedList.get(idx-1);
    }
}
