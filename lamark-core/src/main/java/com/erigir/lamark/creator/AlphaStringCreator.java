package com.erigir.lamark.creator;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class AlphaStringCreator extends StringCreator {
    public AlphaStringCreator()
    {
        super();
        setValidCharactersByString("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public AlphaStringCreator(int size, Random random) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ", size, random);
    }

    public AlphaStringCreator(int size) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ", size);
    }

}
