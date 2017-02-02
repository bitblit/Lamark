/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.supplier;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Supplier that generates lists of integers, optionally bounded.
 *
 * @author cweiss
 * @since 03/2005
 */
public class IntegerListSupplier extends AbstractLamarkComponent implements Supplier<List<Integer>> {

    /**
     * Size of the string to generate (REQUIRED)*
     */
    private int size;

    private Integer lowerBound;
    private Integer upperBound;

    public IntegerListSupplier(int size, Integer lowerBound, Integer upperBound, Random random) {
        super(random);
        this.size = size;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public IntegerListSupplier(int size, Integer lowerBound, Integer upperBound) {
        super();
        this.size = size;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public List<Integer> get() {
        List<Integer> rval = new ArrayList<>(size);

        Integer bound = upperBound;
        if (bound!=null && lowerBound!=null)
        {
            bound-=lowerBound;
        }

        for (int i=0;i<size;i++)
        {
            int next = (bound==null)?rand().nextInt():rand().nextInt(bound);
            next = (lowerBound==null)?next:next+lowerBound;
            rval.add(next);
        }

        return rval;
    }

}