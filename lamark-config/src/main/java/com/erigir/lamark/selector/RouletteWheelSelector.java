package com.erigir.lamark.selector;

import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by cweiss1271 on 3/24/16.
 */
public class RouletteWheelSelector<T> implements Selector<T> {
    public List<Individual<T>> select(List<Individual<T>> sortedList, Random random, int numberToSelect, boolean minimumBest)
    {
        return (minimumBest)?selectForMin(sortedList, random, numberToSelect):selectForMax(sortedList, random, numberToSelect);
    }

    public List<Individual<T>> selectForMax(List<Individual<T>> sortedList, Random random, int numberToSelect)
    {
        Double totalFitness = sortedList.stream().mapToDouble((p) -> p.getFitness()).sum();
        List<Individual<T>> rval = new ArrayList<>(numberToSelect);

        for (int i=0;i<numberToSelect;i++) {
            double rVal = random.nextDouble() * totalFitness;
            double adder = 0;
            int idx = 0;
            while (adder < rVal) {
                adder += sortedList.get(idx).getFitness();
                idx++;
            }

            rval.add(sortedList.get(idx - 1));
        }
        return rval;
    }

    public List<Individual<T>> selectForMin(List<Individual<T>> sortedList, Random random, int numberToSelect)
    {
        // First reverse the sort
        List<Individual<T>> temp = new ArrayList<>(sortedList);
        Collections.reverse(temp);
        Double minimumFitness = sortedList.get(0).getFitness();
        Double totalFitness = sortedList.stream().mapToDouble((p) -> minimumFitness/p.getFitness()).sum();

        List<Individual<T>> rval = new ArrayList<>(numberToSelect);
        for (int i=0;i<numberToSelect;i++)
        {
            double rVal = random.nextDouble()*totalFitness;

            double adder = 0;
            int idx=0;
            while (adder<rVal)
            {
                adder+=(minimumFitness/sortedList.get(idx).getFitness());
                idx++;
            }
            rval.add(sortedList.get(idx-1));
        }

        return rval;
    }


}
