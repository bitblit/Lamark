package com.erigir.lamark.supplier;

import java.util.Random;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class HexadecimalStringSupplier extends StringSupplier {
    public HexadecimalStringSupplier()
    {
        super();
        setValidCharactersByString("0123456789ABCDEF");
    }

    public HexadecimalStringSupplier(int size, Random random) {
        super("0123456789ABCDEF", size, random);
    }

    public HexadecimalStringSupplier(int size) {
        super("0123456789ABCDEF", size);
    }

}
