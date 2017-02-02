package com.erigir.lamark.supplier;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class DecimalStringSupplier extends StringSupplier {
    public DecimalStringSupplier()
    {
        super();
        setValidCharactersByString("0123456789");
    }

    public DecimalStringSupplier(int size, Random random) {
        super("0123456789", size, random);
    }

    public DecimalStringSupplier(int size) {
        super("0123456789", size);
    }

}
