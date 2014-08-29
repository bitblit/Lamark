/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.annotation.Crossover;
import com.erigir.lamark.annotation.Param;
import com.erigir.lamark.annotation.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple two-point crossover for lists of any type.
 * NOTE: Doesnt preserve the permutation property.
 *
 * @author cweiss
 * @since 03/2005
 */
public class ListCrossover {

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    @Crossover
    public List twoPointCrossover(
            @Parent List p1,
            @Parent List p2, @Param("random") Random random) {
        int point1 = random.nextInt(p1.size() - 1); // cant pick last slot
        int point2 = (point1 + 1) + random.nextInt(p1.size() - (point1 + 1)); // use +1 to get at least 1 spread

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

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    @Crossover
    public List singlePointCrossover(
            @Parent List p1,
            @Parent List p2, @Param("random") Random random) {
        int point = random.nextInt(p1.size());

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