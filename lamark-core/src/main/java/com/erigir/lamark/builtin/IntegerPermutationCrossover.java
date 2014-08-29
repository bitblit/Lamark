/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.builtin;

import com.erigir.lamark.annotation.Crossover;
import com.erigir.lamark.annotation.LamarkComponent;
import com.erigir.lamark.annotation.Param;
import com.erigir.lamark.annotation.Parent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@LamarkComponent
public class IntegerPermutationCrossover {


    /**
     * A crossover for Lists of Integers that maintains the permutation property via fill.
     * <p/>
     * <em>Fill</em> in this context means that we pick a split point, and copy in
     * every item to the left of that point into the child.  We then iterate through
     * the second parent, and fill the remaining slots in the permutation out
     * with numbers in the order they appear in the second parent, leaving out any
     * duplicates so as to preserve the permutation property.
     */
    @Crossover
    public List permutationFillCrossover(@Parent List p1,
                                         @Parent List p2, @Param("random") Random random) {
        int size = p1.size();
        int point = random.nextInt(p1.size());

        List c1 = new ArrayList(size);

        for (int i = 0; i < point; i++) {
            c1.add(p1.get(i));
        }

        int index = point;
        int cidx = -1;
        while (c1.size() < size) {
            cidx = (index % size);
            if (!c1.contains(p2.get(cidx))) {
                c1.add(p2.get(cidx));
            }
            index++;
        }

        return c1;
    }


    /**
     * Crosses over two lists of integers preserving both the permutation and longest common subsequence property.
     * <p/>
     * This crossover finds the longest common subsequence of the two parent lists, assuming that the lists have
     * a symmetrical nature (ie, can be reversed without effect on the underlying problem space, so a LCS match on
     * the reverse of a list is as good as a LCS match on it front-ways).  It then copies this LCS into the child,
     * and fills the remainder of the list randomly from the remnants.
     */
    @Crossover
    public List permutationLongestCommonSubsequenceCrossover(@Parent List p1,
                                                             @Parent List p2, @Param("random") Random random) {
        int size = p1.size();

        // Find the largest common substring in any order
        List sub = LongestCommonUtils.computeLongestCommonSubsequence(p1, p2); // Get the longest common subsequence
        ArrayList p1r = new ArrayList(p1);
        ArrayList p2r = new ArrayList(p2);
        Collections.reverse(p1r);
        Collections.reverse(p2r);
        List test = LongestCommonUtils.computeLongestCommonSubsequence(p1, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = LongestCommonUtils.computeLongestCommonSubsequence(p1r, p2);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = LongestCommonUtils.computeLongestCommonSubsequence(p1r, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }

        ArrayList child = new ArrayList(size);

        // Put the LCS into the child
        child.addAll(sub);

        List remainder = new ArrayList(size);
        remainder.addAll(p1);
        remainder.removeAll(sub);
        while (remainder.size() > 0) {
            child.add(remainder.remove(random.nextInt(remainder.size())));
        }

        return child;
    }

    /**
     * Crosses over two lists of integers preserving both the permutation and longest common substring property.
     * <p/>
     * This crossover finds the longest common substring of the two parent lists, assuming that the lists have
     * a symmetrical nature (ie, can be reversed without effect on the underlying problem space, so a LCS match on
     * the reverse of a list is as good as a LCS match on it front-ways).  It then copies this LCS into the child,
     * and fills the remainder of the list randomly from the remnants.
     */
    @Crossover
    public List permutationLongestCommonSubstring(@Parent List p1,
                                                  @Parent List p2, @Param("random") Random random) {
        int size = p1.size();

        // Find the largest common substring in any order
        List sub = LongestCommonUtils.lcs(p1, p2); // Get the longest common subsequence
        ArrayList p1r = new ArrayList(p1);
        ArrayList p2r = new ArrayList(p2);
        Collections.reverse(p1r);
        Collections.reverse(p2r);
        List test = LongestCommonUtils.lcs(p1, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = LongestCommonUtils.lcs(p1r, p2);
        if (test.size() > sub.size()) {
            sub = test;
        }
        test = LongestCommonUtils.lcs(p1r, p2r);
        if (test.size() > sub.size()) {
            sub = test;
        }

        ArrayList c1 = new ArrayList(size);

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


}