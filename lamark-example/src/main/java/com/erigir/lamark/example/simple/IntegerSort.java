/*
 * Created on Feb 22, 2005
 */
package com.erigir.lamark.example.simple;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * This fitness function sorts a list of integers by multiplying the position of
 * an integer by its value.  This tends to create the largest value by putting the
 * largest numbers in the highest positions.  For simple viewing, a permutation
 * should be used instead of a generic list of integers.
 *
 * @author cweiss
 * @since 02/2005
 */
public class IntegerSort implements ToDoubleFunction<List<Integer>> {

    @Override
    public double applyAsDouble(List<Integer> vals) {
        double score = 0;
        for (int i = 0; i < vals.size(); i++) {
            Integer next = (Integer) vals.get(i);
            score += (i * next);
        }
        return score;
    }

}