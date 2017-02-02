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
 * Supplier that creates individuals with a genome of Lists of bytes.
 * Size must be set, or the validator method will add an error.
 *
 * @author cweiss
 * @since 09/2007
 */
public class ByteListSupplier extends AbstractLamarkComponent implements Supplier<List<Byte>> {
    /**
     * Size of the string to generate (REQUIRED)*
     */
    private int size;

    public ByteListSupplier(int size, Random random) {
        super(random);
        this.size = size;
    }

    public ByteListSupplier(int size) {
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