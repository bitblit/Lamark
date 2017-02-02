package com.erigir.lamark.supplier;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class AlphaAndSpaceStringSupplier extends StringSupplier {
    public AlphaAndSpaceStringSupplier()
    {
        super();
        setValidCharactersByString("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
    }

    public AlphaAndSpaceStringSupplier(int size, Random random) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ ", size, random);
    }

    public AlphaAndSpaceStringSupplier(int size) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ ", size);
    }

}
