package com.erigir.lamark.creator;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class HexadecimalStringCreator extends StringCreator {
    public HexadecimalStringCreator()
    {
        super();
        setValidCharactersByString("0123456789ABCDEF");
    }

    public HexadecimalStringCreator(int size, Random random) {
        super("0123456789ABCDEF", size, random);
    }

    public HexadecimalStringCreator(int size) {
        super("0123456789ABCDEF", size);
    }

}
