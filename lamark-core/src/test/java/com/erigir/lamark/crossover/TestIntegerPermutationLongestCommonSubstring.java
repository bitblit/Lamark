package com.erigir.lamark.crossover;

import com.erigir.lamark.builtin.LongestCommonUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestIntegerPermutationLongestCommonSubstring {

    @Test
    public void testLCS() {
        List<Integer> l1 = new ArrayList<Integer>();
        List<Integer> l2 = new ArrayList<Integer>();

        l1.add(1);
        l1.add(2);
        l1.add(3);
        l1.add(4);
        l1.add(5);

        l2.add(4);
        l2.add(5);
        l2.add(1);
        l2.add(2);
        l2.add(3);

        List<Integer> rval = (List<Integer>)LongestCommonUtils.lcs(l1, l2);
        List<Integer> expected = Arrays.asList(1, 2, 3);
        assertThat(rval, is(expected));
    }
}
