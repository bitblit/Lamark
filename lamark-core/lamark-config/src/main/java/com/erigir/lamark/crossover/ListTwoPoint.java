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
 * A simple two-point crossover for lists of any type.
 * NOTE: Doesnt preserve the permutation property.
 *
 * @author cweiss
 * @since 03/2005
 */
public class ListTwoPoint   extends AbstractLamarkComponent implements Function<List<List>,List> {

    public ListTwoPoint() {
    }

    public ListTwoPoint(Random srcRandom) {
        super(srcRandom);
    }

    public List apply(List<List> parents) {
        List p1 = parents.get(0);
        List p2 = parents.get(1);
        int point1 = rand().nextInt(p1.size() - 1); // cant pick last slot
        int point2 = (point1 + 1) + rand().nextInt(p1.size() - (point1 + 1)); // use +1 to get at least 1 spread

        List<Object> c1 = new ArrayList<Object>(p1.size());

        for (int i = 0; i < point1; i++) {
            c1.add(p1.get(i));
        }
        for (int i = point1; i < point2; i++) {
            c1.add(p2.get(i));
        }
        for (int i = point2; i < p1.size(); i++) {
            c1.add(p1.get(i));
        }

        return c1;
    }


}