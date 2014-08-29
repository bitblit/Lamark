/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.builtin;

import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.LamarkComponent;
import com.erigir.lamark.annotation.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creator that generates lists of things.
 *
 * @author cweiss
 * @since 03/2005
 */
@LamarkComponent
public class ListCreator {
    private static final Logger LOG = LoggerFactory.getLogger(ListCreator.class);

    /**
     * Creates a list of random integers
     */
    @Creator
    public List<Integer> createIntegerList(@Param("size") Integer size, @Param("random") Random random) {
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }

        List<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            temp.add(random.nextInt());
        }
        return temp;
    }

    /**
     * Creates a list of random integers between the two bounds
     *
     * @param size
     * @param upperBoundInclusive
     * @param lowerBoundInclusive
     * @param random
     * @return
     */
    @Creator
    public List<Integer> createRangedIntegerList(@Param("size") Integer size,
                                                 @Param("upperBoundInclusive") Integer upperBoundInclusive,
                                                 @Param("lowerBoundInclusive") Integer lowerBoundInclusive,
                                                 @Param("random") Random random
    ) {
        int range = upperBoundInclusive - lowerBoundInclusive;
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        List<Integer> rval = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            rval.add(new Integer(random.nextInt(range)
                    + lowerBoundInclusive));
        }
        return rval;
    }

    /**
     * Creates a list of random integers that is a permutation (all integers from 0..n exactly once in random order)
     */
    @Creator
    public List<Integer> createIntegerPermutation(@Param("size") Integer size, @Param("random") Random random) {
        LOG.debug("create called");
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }

        ArrayList<Integer> rval = new ArrayList<Integer>(size);
        ArrayList<Integer> temp = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            temp.add(new Integer(i));
        }

        int loc = -1;
        for (int i = 0; i < size; i++) {
            loc = random.nextInt(temp.size());
            rval.add(temp.get(loc));
            temp.remove(loc);
        }
        return rval;
    }

    @Creator
    public List<Byte> createByteList(@Param("size") Integer size, @Param("random") Random random) {
        byte[] temp = new byte[size];
        random.nextBytes(temp);

        List<Byte> rval = new ArrayList<Byte>(size);
        for (byte b : temp) {
            rval.add(b);
        }
        return rval;
    }


}