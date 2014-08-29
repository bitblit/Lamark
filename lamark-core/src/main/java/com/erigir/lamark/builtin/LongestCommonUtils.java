/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LongestCommonUtils {

    /**
     * Computes the longest common subsequence of two lists, assuming equal length.
     *
     * @param l1 List 1 to search
     * @param l2 List 2 to search
     * @return List containing the longest common subsequence
     */
    public static List computeLongestCommonSubsequence(List l1, List l2) {
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
    private static List backTrack(int[][] c, List x, List y, int i, int j) {
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

    /**
     * Construct the longest common substring between two strings if such
     * a substring exists. Note that this is different from the longest
     * common subsequence in that it assumes you want the longest
     * continuous sequence. The cost of this routine can be made less by
     * keeping a master copy of data around that you want to check input
     * against. That is, imagine that you keep the sorted suffix arrays
     * around for some collection of data items. Then finding the LCS
     * against that set is just a matter of computing the suffix matrix
     * for the input (e.g., line) and comparing against the pre-computed
     * suffix arrays for each data item.
     * <p/>
     * In any event, this routine always computes and sorts the suffix
     * arrays for both input string parameters.
     *
     * @param arr1 the first string instance
     * @param arr2 the second string instance
     * @return the longest common substring, or the empty string if
     * at least one of the arguments are <code>null</code>, empty,
     * or there is no match.
     */
    public static List<? extends Comparable> lcs(List<? extends Comparable> arr1, List<? extends Comparable> arr2) {
        ArrayList empty = new ArrayList();
       /* BEFORE WE ALLOCATE ANY DATA STORAGE, VALIDATE ARGS */
        if (null == arr1 || 0 == arr1.size())
            return empty;
        if (null == arr2 || 0 == arr2.size())
            return empty;

        if (equalLists(arr1, arr2)) {
            return arr1;
        }

       /* ALLOCATE VARIABLES WE'LL NEED FOR THE ROUTINE */
        ArrayList bestMatch = new ArrayList();
        ArrayList currentMatch = new ArrayList();
        ArrayList<List<? extends Comparable>> dataSuffixList = new ArrayList<List<? extends Comparable>>();
        ArrayList<List<? extends Comparable>> lineSuffixList = new ArrayList<List<? extends Comparable>>();

       /* FIRST, COMPUTE SUFFIX ARRAYS */
        for (int i = 0; i < arr1.size(); i++) {
            dataSuffixList.add(arr1.subList(i, arr1.size()));
        }
        for (int i = 0; i < arr2.size(); i++) {
            lineSuffixList.add(arr2.subList(i, arr2.size()));
        }

       /* STANDARD SORT SUFFIX ARRAYS */
        ListComparator comp = new ListComparator();
        Collections.sort(dataSuffixList, comp);
        Collections.sort(lineSuffixList, comp);

       /* NOW COMPARE ARRAYS MEMBER BY MEMBER */
        List<?> d = null;
        List<?> l = null;
        List<?> shorterTemp = null;
        int stopLength = 0;
        int k = 0;
        boolean match = false;

        bestMatch.retainAll(empty);
        bestMatch.addAll(currentMatch);
        for (int i = 0; i < dataSuffixList.size(); i++) {
            d = (List) dataSuffixList.get(i);
            for (int j = 0; j < lineSuffixList.size(); j++) {
                l = (List) lineSuffixList.get(j);
                if (d.size() < l.size()) {
                    shorterTemp = d;
                } else {
                    shorterTemp = l;
                }

                currentMatch.retainAll(empty);
                k = 0;
                stopLength = shorterTemp.size();
                match = (l.get(k).equals(d.get(k)));
                while (k < stopLength && match) {
                    if (l.get(k).equals(d.get(k))) {
                        currentMatch.add((Integer) shorterTemp.get(k));
                        k++;
                    } else {
                        match = false;
                    }
                }
                if (currentMatch.size() > bestMatch.size()) {
                    bestMatch.retainAll(empty);
                    bestMatch.addAll(currentMatch);
                }
            }
        }
        return bestMatch;
    }

    /**
     * Checks if 2 lists are equal
     *
     * @param l1 List 1 to compare
     * @param l2 List 2 to compare
     * @return true if lists are equal
     */
    private static boolean equalLists(List l1, List l2) {
        if (l1 == l2) {
            return true;
        }
        if (l1 == null || l2 == null) // but not both, see above
        {
            return false;
        }
        if (l1.size() != l2.size()) {
            return false;
        }
        boolean match = true;
        for (int i = 0; i < l1.size() && match; i++) {
            if (!l1.get(i).equals(l2.get(i))) {
                match = false;
            }
        }
        return match;
    }

    /**
     * An implementation of comparator for lists that sorts them by the first unequal element smallest to top
     *
     * @author cweiss
     * @since 03/2005
     */
    static class ListComparator implements Comparator<List<? extends Comparable>> {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(List<? extends Comparable> o1, List<? extends Comparable> o2) {
            int rval = 0;

            for (int i = 0; i < o1.size() && i < o2.size() && rval == 0; i++) {
                Comparable c1 = o1.get(i);
                Comparable c2 = o2.get(i);
                rval = c1.compareTo(c2);
            }

            if (rval == 0) {
                rval = o1.size() - o2.size();
            }

            return rval;
        }
    }


}