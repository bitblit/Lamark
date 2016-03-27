package com.erigir.lamark.creator;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class AlphaAndSpaceStringCreator extends StringCreator {
    public AlphaAndSpaceStringCreator()
    {
        super();
        setValidCharactersByString("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
    }

    public AlphaAndSpaceStringCreator(int size, Random random) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ ", size, random);
    }

    public AlphaAndSpaceStringCreator(int size) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ ", size);
    }

}
