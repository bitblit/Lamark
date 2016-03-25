/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.*;
import java.util.function.Function;


/**
 * Crosses over two lists of integers preserving both the permutation and longest common substring property.
 * &lt;p /&gt;
 * This crossover finds the longest common substring of the two parent lists, assuming that the lists have
 * a symmetrical nature (ie, can be reversed without effect on the underlying problem space, so a LCS match on
 * the reverse of a list is as good as a LCS match on it front-ways).  It then copies this LCS into the child,
 * and fills the remainder of the list randomly from the remnants.
 *
 * @author cweiss
 * @since 03/2005
 */
public class IntegerPermutationLongestCommonSubstring extends AbstractLamarkComponent implements Function<List<List<Integer>>,List<Integer>> {

    public IntegerPermutationLongestCommonSubstring() {
    }

    public IntegerPermutationLongestCommonSubstring(Random srcRandom) {
        super(srcRandom);
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
     * &lt;p /&gt;
     * In any event, this routine always computes and sorts the suffix
     * arrays for both input string parameters.
     *
     * @param arr1 the first string instance
     * @param arr2 the second string instance
     * @return the longest common substring, or the empty string if
     * at least one of the arguments are <code>null</code>, empty,
     * or there is no match.
     */
    public static List<Integer> lcs(List<Integer> arr1, List<Integer> arr2) {
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
        ArrayList<Integer> bestMatch = new ArrayList<Integer>();
        ArrayList<Integer> currentMatch = new ArrayList<Integer>();
        ArrayList<List<Integer>> dataSuffixList = new ArrayList<List<Integer>>();
        ArrayList<List<Integer>> lineSuffixList = new ArrayList<List<Integer>>();

       /* FIRST, COMPUTE SUFFIX ARRAYS */
        for (int i = 0; i < arr1.size(); i++) {
            dataSuffixList.add(arr1.subList(i, arr1.size()));
        }
        for (int i = 0; i < arr2.size(); i++) {
            lineSuffixList.add(arr2.subList(i, arr2.size()));
        }

       /* STANDARD SORT SUFFIX ARRAYS */
        IntegerListComparator comp = new IntegerListComparator();
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

    public List<Integer> apply(List<List<Integer>> parents) {
        List<Integer> p1 = parents.get(0);
        List<Integer> p2 = parents.get(1);
        int size = p1.size();

        // Find the largest common substring in any order
        List sub = lcs(p1, p2); // Get the longest common subsequence
        ArrayList<Integer> p1r = new ArrayList<Integer>(p1);
        ArrayList<Integer> p2r = new ArrayList<Integer>(p2);
        Collections.reverse(p1r);
        Collections.reverse(p2r);
        List test = lcs(p1, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = lcs(p1r, p2);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = lcs(p1r, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }

        ArrayList<Integer> c1 = new ArrayList<Integer>(size);

        // Build child 1
        int otherParentIdx = 0;
        for (int i = 0; i < size; i++) {
            if (sub.contains(p1.get(i))) {
                c1.add((Integer) p1.get(i));
            } else {
                while (sub.contains(p2.get(otherParentIdx % size))) {
                    otherParentIdx++;
                }
                c1.add((Integer) p2.get(otherParentIdx % size));
                otherParentIdx++;
            }
        }

        Collections.reverse(c1);

        return c1;
    }

    /**
     * An implementation of comparator for lists that sorts them by the first unequal element smallest to top
     *
     * @author cweiss
     * @since 03/2005
     */
    static class IntegerListComparator implements Comparator<List<Integer>> {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(List<Integer> o1, List<Integer> o2) {
            int rval = 0;

            for (int i = 0; i < o1.size() && i < o2.size() && rval == 0; i++) {
                Integer c1 = o1.get(i);
                Integer c2 = o2.get(i);
                rval = c1.compareTo(c2);
            }

            if (rval == 0) {
                rval = o1.size() - o2.size();
            }

            return rval;
        }
    }

}