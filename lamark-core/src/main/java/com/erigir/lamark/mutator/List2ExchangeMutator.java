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
 * Performs the permutation-safe 2-exchange mutation.
 * &lt;p /&gt;
 * If the original list is viewed as a path, this mutation can
 * be viewed as cutting the string 2 in 2 places, and tying the
 * opposite ends together.
 *
 * @author cweiss
 * @since 02/2005
 */
public class List2ExchangeMutator extends AbstractLamarkComponent implements Function<List,List> {

    public List2ExchangeMutator() {
        super();
    }

    public List2ExchangeMutator(Random srcRandom) {
        super(srcRandom);
    }

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

    public List apply(List input) {

        int loc1 = rand().nextInt(input.size());
        int loc2 = rand().nextInt(input.size());

        return exchange2(input, Math.min(loc1, loc2), Math.max(loc1, loc2));
    }

}