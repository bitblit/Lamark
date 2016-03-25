/*
 * Created on Feb 17, 2005
 *  
 */
package com.erigir.lamark.mutator;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;


/**
 * Simple, permutation safe 2 point array switch.
 * Picks 2 points and exchanges them.  Does not exercise any
 * unused portions of the input alphabet.
 *
 * @author cweiss
 * @since 04/2005
 */
public class ListSimpleMutator  extends AbstractLamarkComponent implements Function<List,List> {

    public ListSimpleMutator() {
        super();
    }

    public ListSimpleMutator(Random srcRandom) {
        super(srcRandom);
    }

    public List apply(List being) {
        List<Object> chromosome = new ArrayList<Object>(being.size());
        chromosome.addAll(being);
        int loc1 = rand().nextInt(chromosome.size());
        int loc2 = rand().nextInt(chromosome.size());
        Object temp = chromosome.get(loc2);
        chromosome.set(loc2, chromosome.get(loc1));
        chromosome.set(loc1, temp);
        return chromosome;
    }

}