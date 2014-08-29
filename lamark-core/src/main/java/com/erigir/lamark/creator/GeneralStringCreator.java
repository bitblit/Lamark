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
 * Creates new individuals with varying character sets
 *
 * @author cweiss
 * @since 04/2006
 */
public class GeneralStringCreator {
    private static String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String ALPHA_WITH_SPACE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    private static String BINARY = "01";
    private static String DECIMAL = "0123456789";
    private static String HEXADECIMAL = "0123456789ABCDEF";

    @Creator
    public String createAlphaString(@Param("size")Integer size, @Param("random")Random random )
    {
        return createString(size, random, ALPHA);
    }

    @Creator
    public String createAlphaWithSpaceString(@Param("size")Integer size, @Param("random")Random random )
    {
        return createString(size, random, ALPHA_WITH_SPACE);
    }

    @Creator
    public String createBinaryString(@Param("size")Integer size, @Param("random")Random random )
    {
        return createString(size, random, BINARY);
    }

    @Creator
    public String createDecimalString(@Param("size")Integer size, @Param("random")Random random )
    {
        return createString(size, random, DECIMAL);
    }

    @Creator
    public String createHexadecimalString(@Param("size")Integer size, @Param("random")Random random )
    {
        return createString(size, random, HEXADECIMAL);
    }

    @Creator
    public String createString(@Param("size")Integer size, @Param("random")Random random, @Param("validCharacters")String validCharacters)
    {
        if (validCharacters == null || validCharacters.length() == 0) {
            throw new IllegalStateException("Not initialized, or no valid characters");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(validCharacters.charAt(random.nextInt(validCharacters
                    .length())));
        }
        return sb.toString();
    }

}