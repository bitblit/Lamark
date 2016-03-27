package com.erigir.lamark.creator;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class DecimalStringCreator extends StringCreator {
    public DecimalStringCreator()
    {
        super();
        setValidCharactersByString("0123456789");
    }

    public DecimalStringCreator(int size, Random random) {
        super("0123456789", size, random);
    }

    public DecimalStringCreator(int size) {
        super("0123456789", size);
    }

}
