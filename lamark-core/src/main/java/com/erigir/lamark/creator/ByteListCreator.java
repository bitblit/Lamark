/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Creator that creates individuals with a genome of Lists of bytes.
 * Size must be set, or the validator method will add an error.
 *
 * @author cweiss
 * @since 09/2007
 */
public class ByteListCreator extends AbstractLamarkComponent implements Supplier<List<Byte>> {
    /**
     * Size of the string to generate (REQUIRED)*
     */
    private int size;

    public ByteListCreator(int size, Random random) {
        super(random);
        this.size = size;
    }

    public ByteListCreator(int size) {
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

    public List<Byte> get() {
        byte[] temp = new byte[size];
        rand().nextBytes(temp);

        List<Byte> rval = new ArrayList<Byte>(size);
        for (byte b : temp) {
            rval.add(b);
        }
        return rval;
    }

}