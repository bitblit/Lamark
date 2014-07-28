/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.Individual;
import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.LamarkComponent;
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
@LamarkComponent
public class GeneralStringCreator {
    private static List<Character> ALPHA = stringToCharacterList("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    private static List<Character> ALPHA_WITH_SPACE = stringToCharacterList("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
    private static List<Character> BINARY = stringToCharacterList("01");
    private static List<Character> DECIMAL = stringToCharacterList("0123456789");
    private static List<Character> HEXADECIMAL = stringToCharacterList("0123456789ABCDEF");

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


    private static List<Character> stringToCharacterList(String input)
    {
        List<Character> rval = new ArrayList<>(input.length());
        for (int i=0;i<input.length();i++)
        {
            Character c = input.charAt(i);
            if (!rval.contains(c))
            {
                rval.add(c);
            }
        }
        return rval;
    }

    private String createString(int size, Random random, List<Character> validCharacters)
    {
        if (validCharacters == null || validCharacters.size() == 0) {
            throw new IllegalStateException("Not initialized, or no valid characters");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(validCharacters.get(random.nextInt(validCharacters
                    .size())));
        }
        return sb.toString();
    }

}