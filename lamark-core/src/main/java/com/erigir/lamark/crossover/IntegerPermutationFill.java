/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * A crossover for Lists of Integers that maintains the permutation property via fill.
 * <p/>
 * <em>Fill</em> in this context means that we pick a split point, and copy in
 * every item to the left of that point into the child.  We then iterate through
 * the second parent, and fill the remaining slots in the permutation out
 * with numbers in the order they appear in the second parent, leaving out any
 * duplicates so as to preserve the permutation property.
 *
 * @author cweiss
 * @since 03/2005
 */
public class IntegerPermutationFill extends AbstractLamarkComponent implements ICrossover<List<Integer>> {


    /**
     * @see com.erigir.lamark.ICrossover#parentCount()
     */
    public int parentCount() {
        return 2;
    }

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    public Individual<List<Integer>> crossover(List<Individual<List<Integer>>> parents) {
        List<Integer> p1 = parents.get(0).getGenome();
        List<Integer> p2 = parents.get(1).getGenome();
        int size = p1.size();
        int point = getLamark().getRandom().nextInt(p1.size());

        List<Integer> c1 = new ArrayList<Integer>(size);

        for (int i = 0; i < point; i++) {
            c1.add((Integer) p1.get(i));
        }

        int index = point;
        int cidx = -1;
        while (c1.size() < size) {
            cidx = (index % size);
            if (!c1.contains(p2.get(cidx))) {
                c1.add((Integer) p2.get(cidx));
            }
            index++;
        }

        return new Individual<List<Integer>>(c1);
    }


}