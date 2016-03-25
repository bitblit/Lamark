/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICreator;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;
import com.erigir.lamark.annotation.Creator;

import java.util.ArrayList;
import java.util.List;


/**
 * Creates individuals as lists of integers, with each integer taken from the supplied range.
 *
 * @author cweiss
 * @since 04/2006
 */
public class RangedIntegerListCreator extends AbstractLamarkComponent implements ICreator<List<Integer>>, IValidatable {
    /**
     * Lower bound for the integers to be created *
     */
    private int lowerBoundInclusive = Integer.MIN_VALUE;
    /**
     * Upper bound for the integers to be created *
     */
    private int upperBoundInclusive = Integer.MAX_VALUE;
    /**
     * Size of the list to generate (REQUIRED)*
     */
    private Integer size;

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }


    /**
     * Mutator method
     *
     * @param size new value
     */
    public void setSize(Integer size) {
        this.size = size;
    }


    /**
     * @see com.erigir.lamark.ICreator#create()
     */
    public Individual create() {
        int range = upperBoundInclusive - lowerBoundInclusive;
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        List<Integer> rval = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            rval.add(new Integer(getLamark().getRandom().nextInt(range)
                    + lowerBoundInclusive));
        }
        Individual i = new Individual();
        i.setGenome(rval);
        return i;
    }

    @Creator
    public List<Integer> createIntegerList()
    {
        int range = upperBoundInclusive - lowerBoundInclusive;
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        List<Integer> rval = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            rval.add(new Integer(getLamark().getRandom().nextInt(range)
                    + lowerBoundInclusive));
        }
        return rval;
    }

    /**
     * Accessor method
     *
     * @return int containing the property
     */
    public int getLowerBoundInclusive() {
        return lowerBoundInclusive;
    }

    /**
     * Mutator method
     *
     * @param lowerBoundInclusive new value
     */
    public void setLowerBoundInclusive(int lowerBoundInclusive) {
        this.lowerBoundInclusive = lowerBoundInclusive;
    }

    /**
     * Accessor method
     *
     * @return int containing the property
     */
    public int getUpperBoundInclusive() {
        return upperBoundInclusive;
    }

    /**
     * Mutator method
     *
     * @param upperBoundInclusive new value
     */
    public void setUpperBoundInclusive(int upperBoundInclusive) {
        this.upperBoundInclusive = upperBoundInclusive;
    }


    /**
     * Checks if size was set.
     *
     * @see com.erigir.lamark.IValidatable#validate(List)
     */
    public void validate(List<String> errors) {
        if (size == null) {
            errors.add("No 'size' set for the creator");
        }
    }


}