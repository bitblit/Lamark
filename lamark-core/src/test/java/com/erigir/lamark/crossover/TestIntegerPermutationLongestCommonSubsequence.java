package com.erigir.lamark.crossover;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class TestIntegerPermutationLongestCommonSubsequence {
    @Test
    public void testLCS() {
        List<Integer> l1 = new ArrayList<Integer>();
        List<Integer> l2 = new ArrayList<Integer>();

        l1.add(1);
        l1.add(2);
        l1.add(3);
        l1.add(4);
        l1.add(5);
        l1.add(6);
        l1.add(7);
        l1.add(8);

        l2.add(1);
        l2.add(5);
        l2.add(4);
        l2.add(2);
        l2.add(3);
        l2.add(7);
        l2.add(8);
        l2.add(6);

        List<Integer> rval = IntegerPermutationLongestCommonSubsequence.computeLongestCommonSubsequence(l1, l2);
        List<Integer> expected = Arrays.asList(2, 3, 7, 8);
        assertThat(rval, is(expected));
    }
}
