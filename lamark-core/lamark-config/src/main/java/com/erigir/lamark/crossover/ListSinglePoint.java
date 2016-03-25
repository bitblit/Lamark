/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;


/**
 * A simple single-point crossover for lists (of any type).
 * NOTE: Doesn't preserve permutation property.
 *
 * @author cweiss
 * @since 03/2005
 */
public class ListSinglePoint  extends AbstractLamarkComponent implements Function<List<List>,List> {

    public ListSinglePoint() {
    }

    public ListSinglePoint(Random srcRandom) {
        super(srcRandom);
    }

    public List apply(List<List> parents) {
        List p1 = parents.get(0);
        List p2 = parents.get(1);
        int point = rand().nextInt(p1.size());

        List<Object> c1 = new ArrayList<Object>(p1.size());

        for (int i = 0; i < point; i++) {
            c1.add(p1.get(i));
        }
        for (int i = point; i < p1.size(); i++) {
            c1.add(p2.get(i));
        }

        return c1;
    }

}