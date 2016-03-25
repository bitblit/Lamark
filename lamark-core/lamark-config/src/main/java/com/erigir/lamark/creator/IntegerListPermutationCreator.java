/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;


/**
 * Creator that generates lists of integers that have the permuation property (0..size)
 *
 * @author cweiss
 * @since 04/2006
 */
public class IntegerListPermutationCreator extends AbstractLamarkComponent implements Supplier<List<Integer>> {
    /**
     * Size of the list to generate (REQUIRED)*
     */
    private Integer size;

    public IntegerListPermutationCreator(int size, Random random) {
        super(random);
        this.size = size;
    }

    public IntegerListPermutationCreator(int size) {
        super();
        this.size = size;
    }

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }


    public List<Integer> get() {
        ArrayList<Integer> rval = new ArrayList<Integer>(size);
        ArrayList<Integer> temp = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            temp.add(new Integer(i));
        }

        int loc = -1;
        for (int i = 0; i < size; i++) {
            loc = rand().nextInt(temp.size());
            rval.add(temp.get(loc));
            temp.remove(loc);
        }
        return rval;
    }

}