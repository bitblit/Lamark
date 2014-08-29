package com.erigir.lamark.crossover;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestStringSinglePoint {

    @Test
    public void testCrossover() {
        int size = 1000000;
        String p1 = "0000";
        String p2 = "1111";

        Individual<String> i1 = new Individual<String>(p1);
        Individual<String> i2 = new Individual<String>(p2);
        List<Individual<String>> parents = new ArrayList<Individual<String>>(2);
        parents.add(i1);
        parents.add(i2);

        StringSinglePoint ssp = new StringSinglePoint();
        ssp.setLamark(new Lamark(null));
        int case1 = 0;
        int case2 = 0;
        int case3 = 0;
        for (int i = 0; i < size; i++) {
            String c1 = ssp.crossover(parents).getGenome();

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
