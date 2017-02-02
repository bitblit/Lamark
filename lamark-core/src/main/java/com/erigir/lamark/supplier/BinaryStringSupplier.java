package com.erigir.lamark.supplier;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class BinaryStringSupplier extends StringSupplier {
    public BinaryStringSupplier()
    {
        super();
        setValidCharactersByString("01");
    }

    public BinaryStringSupplier(int size, Random random) {
        super("01", size, random);
    }

    public BinaryStringSupplier(int size) {
        super("01", size);
    }

}
