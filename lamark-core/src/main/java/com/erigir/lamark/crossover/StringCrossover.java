/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.annotation.Crossover;
import com.erigir.lamark.annotation.Param;
import com.erigir.lamark.annotation.Parent;

import java.util.Random;


/**
 * Simple single-point crossover for strings.
 *
 * @author cweiss
 * @since 03/2005
 */
public class StringCrossover {

    @Crossover
    public String crossoverString(
            @Parent String p1,
            @Parent String p2, @Param("random") Random random) {
        // len = 4, pos = 0,1,2,3  valid = 1,2

        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Cant crossover, one of the parents is null");
        }
        if (p1.length() == 0 || p2.length() == 0) {
            throw new IllegalArgumentException("Cant crossover, length of one of the parents is 0");
        }


        int point = 1 + random.nextInt(p1.length() - 1);

        return p1.substring(0, point) + p2.substring(point);
    }

}