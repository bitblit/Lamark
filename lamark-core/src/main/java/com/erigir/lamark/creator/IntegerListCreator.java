/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICreator;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator that generates lists of integers.
 *
 * @author cweiss
 * @since 03/2005
 */
public class IntegerListCreator extends AbstractLamarkComponent implements ICreator, IValidatable {
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
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }

        List<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            temp.add(getLamark().getRandom().nextInt());
        }
        Individual i = new Individual();
        i.setGenome(temp);
        return i;
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