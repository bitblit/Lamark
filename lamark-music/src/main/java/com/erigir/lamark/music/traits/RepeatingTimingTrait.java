package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.SubstringCalculationTrie;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This trait attempts to find repeating "themes" in the timing of
 * music, by finding repeating substrings in the notes
 * and giving points for them.
 * <p/>
 * NOTE: Since a given theme can occur anywhere on a scale,
 * the notes themselves are not analyzed, but rather the
 * deltas, so 40-43-41 == 71-74-72
 *
 * @author cweiss
 */

public class RepeatingTimingTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {
        List deltas = getScoreAnalysis().getTimeDeltaList();
        SubstringCalculationTrie lst = SubstringCalculationTrie.build(deltas);
        Map<List, Integer> substrings = lst.substringMap();
        // Build the count map
        Map<Integer, Integer> countMap = new TreeMap<Integer, Integer>(new ReverseComparator());
        for (Iterator<List> i = substrings.keySet().iterator(); i.hasNext(); ) {
            List l = i.next();
            Integer size = substrings.get(l);
            Integer found = countMap.get(l.size());
            if (found == null) {
                found = size;
            } else {
                found += size;
            }
            countMap.put(l.size(), found);
        }

        // From the count map, remove duplicate substrings
        // For example, In BANANA, ANA repeats, but contains NA.  NA shouldnt be counted
        // when its part of ANA, but should be otherwise.  This may cause the
        // substring to be removed entirely if its count goes to 0
        // Also, remove any substrings of length less than 2
        int remCount = 0;
        for (Iterator<Integer> i = countMap.keySet().iterator(); i.hasNext(); ) {
            Integer key = i.next();
            Integer value = countMap.get(key);
            countMap.put(key, value - remCount);
            if (((value - remCount) < 1) || key < 2) {
                i.remove();
            }
            remCount = value;
        }

        // Now generate a score based on the remaining substrings
        double substringMultiple = 0.0;
        for (Iterator<Integer> i = countMap.keySet().iterator(); i.hasNext(); ) {
            Integer key = i.next();
            Integer value = countMap.get(key);
            substringMultiple += key * value;
        }
        double pctOfSong = Math.min(1.0, substringMultiple / (double) deltas.size());
        return pctOfSong * 100.0;

        //double scoreSizeSquared = deltas.size();
        //scoreSizeSquared *= scoreSizeSquared;
        //scoreSizeSquared/=2;

        // Return normalized to 100
        //return Math.min(1.0,substringMultiple/scoreSizeSquared)*100.0;
    }

    /**
     * A simple comparator for sorting integers in reverse order
     *
     * @author cweiss
     */
    static class ReverseComparator implements Comparator<Integer> {

        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }

    }
}
