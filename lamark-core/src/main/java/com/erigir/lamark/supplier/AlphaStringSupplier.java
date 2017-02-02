package com.erigir.lamark.supplier;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class AlphaStringSupplier extends StringSupplier {
    public AlphaStringSupplier()
    {
        super();
        setValidCharactersByString("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public AlphaStringSupplier(int size, Random random) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ", size, random);
    }

    public AlphaStringSupplier(int size) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZ", size);
    }

}
