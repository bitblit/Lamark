/*
 * Created on Feb 17, 2005
 *  
 */
package com.erigir.lamark.builtin;

import com.erigir.lamark.annotation.LamarkComponent;
import com.erigir.lamark.annotation.Mutator;
import com.erigir.lamark.annotation.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@LamarkComponent
public class ListSimpleMutator {

    /**
     * Switches the 2 points in the passed list.
     *
     * @param data   List containing the points to switch
     * @param point1 int containing the index of the first point
     * @param point2 int containing the index of the second point
     * @return List post-modification
     */
    public static List<Object> exchange2(List<?> data, int point1, int point2) {
        List<Object> rval = new ArrayList<Object>(data.size());
        for (Object o : data) {
            rval.add(o);
        }

        if (data != null && point1 != point2 && point2 < data.size()
                && point1 < point2 && point1 > 0) {

            // Pull out the subarray
            List<Object> temp = new ArrayList<Object>(point2 - point1);
            for (int i = point2; i > point1; i--) {
                temp.add(data.get(i));
            }
            for (int i = point1 + 1; i <= point2; i++) {
                rval.set(i, temp.get(i - (point1 + 1)));
            }
        }
        return rval;
    }

    /**
     * Simple, permutation safe 2 point array switch.
     * Picks 2 points and exchanges them.  Does not exercise any
     * unused portions of the input alphabet.
     */
    @Mutator
    public List singleMoveMutator(List tempChromosome, @Param("random") Random random) {
        List<Object> chromosome = new ArrayList<Object>(tempChromosome.size());
        chromosome.addAll(tempChromosome);
        int loc1 = random.nextInt(chromosome.size());
        int loc2 = random.nextInt(chromosome.size());
        Object temp = chromosome.get(loc2);
        chromosome.set(loc2, chromosome.get(loc1));
        chromosome.set(loc1, temp);
        return chromosome;
    }

    /**
     * Picks two points in the list and swaps the sublist
     */
    @Mutator
    public List mutate(List chromosome, @Param("random") Random random) {

        int loc1 = random.nextInt(chromosome.size());
        int loc2 = random.nextInt(chromosome.size());

        return exchange2(chromosome, Math.min(loc1, loc2), Math.max(loc1, loc2));
    }


}