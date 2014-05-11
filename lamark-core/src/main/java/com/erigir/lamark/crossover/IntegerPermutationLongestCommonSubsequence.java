/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Crosses over two lists of integers preserving both the permutation and longest common subsequence property.
 * <p/>
 * This crossover finds the longest common subsequence of the two parent lists, assuming that the lists have
 * a symmetrical nature (ie, can be reversed without effect on the underlying problem space, so a LCS match on
 * the reverse of a list is as good as a LCS match on it front-ways).  It then copies this LCS into the child,
 * and fills the remainder of the list randomly from the remnants.
 *
 * @author cweiss
 * @since 03/2005
 */
public class IntegerPermutationLongestCommonSubsequence extends AbstractLamarkComponent implements
        ICrossover<List<Integer>> {

    /**
     * @see com.erigir.lamark.ICrossover#parentCount()
     */
    public int parentCount() {
        return 2;
    }

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    public Individual<List<Integer>> crossover(List<Individual<List<Integer>>> parents) {
        List<Integer> p1 = parents.get(0).getGenome();
        List<Integer> p2 = parents.get(1).getGenome();
        int size = p1.size();

        // Find the largest common substring in any order
        List sub = computeLongestCommonSubsequence(p1, p2); // Get the longest common subsequence
        ArrayList<Integer> p1r = new ArrayList<Integer>(p1);
        ArrayList<Integer> p2r = new ArrayList<Integer>(p2);
        Collections.reverse(p1r);
        Collections.reverse(p2r);
        List test = computeLongestCommonSubsequence(p1, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = computeLongestCommonSubsequence(p1r, p2);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = computeLongestCommonSubsequence(p1r, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }

        ArrayList<Integer> child = new ArrayList<Integer>(size);

        // Put the LCS into the child
        child.addAll(sub);

        List<Integer> remainder = new ArrayList<Integer>(size);
        remainder.addAll(p1);
        remainder.removeAll(sub);
        while (remainder.size() > 0) {
            child.add(remainder.remove(getLamark().getRandom().nextInt(remainder.size())));
        }

        return new Individual<List<Integer>>(child);
    }


    /**
     * Computes the longest common subsequence of two lists, assuming equal length.
     *
     * @param l1 List 1 to search
     * @param l2 List 2 to search
     * @return List containing the longest common subsequence
     */
    public static List<Integer> computeLongestCommonSubsequence(List<Integer> l1, List<Integer> l2) {
        // Build the c table
        if (l1 == null || l2 == null || l1.size() == 0 || l2.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        if (l1.size() != l2.size()) {
            throw new IllegalArgumentException("CANT HAPPEN: Different sizes: This is not a generalized LCS implementation");
        }

        int size = l1.size();
        int[][] c = new int[size][size];

        int maxVal = -1;
        int maxX = -1;
        int maxY = -1;

        // Build the c table
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (l1.get(x) == l2.get(y)) {
                    if (x == 0 || y == 0) {
                        c[x][y] = 1;
                    } else {
                        c[x][y] = c[x - 1][y - 1] + 1;
                    }
                    // Update the tracked largest value
                    if (c[x][y] > maxVal) {
                        maxVal = c[x][y];
                        maxX = x;
                        maxY = y;
                    }
                } else {
                    if (x == 0 || y == 0) {
                        c[x][y] = 0;
                    } else {
                        c[x][y] = Math.max(c[x][y - 1], c[x - 1][y]);
                    }
                }
            }
        }

        // Now backtrack out the LCS

        if (maxX == -1 || maxY == -1 || maxVal == 1) // longest is 1 .. invalid, just return
        {
            return Collections.EMPTY_LIST;
        }
        return backTrack(c, l1, l2, size - 1, size - 1);
    }

    /**
     * Backtracks the cost matrix of the longest common subsequence to build the LCS.
     * NOTE: This function is called recursively to build the list.
     *
     * @param c int array containing cost matrix
     * @param x List 1 of integers
     * @param y List 2 of integers
     * @param i int position in first list
     * @param j int position in second list
     * @return List of integers representing the lcs
     */
    private static List<Integer> backTrack(int[][] c, List<Integer> x, List<Integer> y, int i, int j) {
        if (i == -1 || j == -1) {
            return Collections.EMPTY_LIST;
        }

        if (x.get(i) == y.get(j)) {
            ArrayList rval = new ArrayList();
            rval.addAll(backTrack(c, x, y, i - 1, j));
            rval.add(x.get(i));
            return rval;
        } else {
            if (i < 1 || j < 1) {
                return Collections.EMPTY_LIST;
            } else if (c[i][j - 1] > c[i - 1][j]) {
                return backTrack(c, x, y, i, j - 1);
            } else {
                return backTrack(c, x, y, i - 1, j);
            }
        }

    }


}