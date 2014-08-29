package com.erigir.lamark.crossover;

import com.erigir.lamark.builtin.StringCrossover;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestStringSinglePoint {

    @Test
    public void testCrossover() {
        Random random = new Random(1);
        int size = 1000000;
        String p1 = "0000";
        String p2 = "1111";

        StringCrossover ssp = new StringCrossover();
        int case1 = 0;
        int case2 = 0;
        int case3 = 0;
        for (int i = 0; i < size; i++) {
            String c1 = ssp.crossoverString(p1,p2,random);

            if (c1.equals("0111")) {
                case1++;
            } else if (c1.equals("0011")) {
                case2++;
            } else if (c1.equals("0001")) {
                case3++;
            } else {
                fail("Impossible child [" + c1 + "]");
            }
        }

        double pc1 = (double) case1 / size;
        double pc2 = (double) case2 / size;
        double pc3 = (double) case3 / size;

        // For valid randomizer and correct algorithm and sufficient size,
        // none of these should be far away from 33%
        assertTrue(pc1 < .34);
        assertTrue(pc2 < .34);
        assertTrue(pc3 < .34);

    }
}
