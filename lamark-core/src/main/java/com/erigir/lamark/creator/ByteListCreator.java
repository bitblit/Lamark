/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Creator that creates individuals with a genome of Lists of bytes.
 *
 * @author cweiss
 * @since 09/2007
 */
public class ByteListCreator {

    /**
     * Creates a list of random integers
     */
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