package com.erigir.lamark.creator;

import java.util.Random;
import java.util.Set;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class BinaryStringCreator extends StringCreator {
    public BinaryStringCreator()
    {
        super();
        setValidCharactersByString("01");
    }

    public BinaryStringCreator(int size, Random random) {
        super("01", size, random);
    }

    public BinaryStringCreator(int size) {
        super("01", size);
    }

}
