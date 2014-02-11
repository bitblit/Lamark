package com.erigir.lamark.music;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestLST {
    @Test
    public void testSubstring()
    {
        List<Integer> song = new ArrayList<Integer>();

        song.add(76);
        song.add(74);
        song.add(72);
        song.add(74);
        song.add(76);
        song.add(76);
        song.add(76);
        song.add(74);
        song.add(74);
        song.add(74);
        song.add(76);
        song.add(79);
        song.add(79);
        song.add(76);
        song.add(74);
        song.add(72);
        song.add(74);
        song.add(76);
        song.add(76);
        song.add(76);
        song.add(76);
        song.add(74);
        song.add(74);
        song.add(76);
        song.add(74);
        song.add(72);
        SubstringCalculationTrie lst = SubstringCalculationTrie.build(ScoreAnalysis.toDeltaList(song));
        System.out.println(lst.toString());
        System.out.println("\n\nSubs:" + lst.substringMap());

        Map<List, Integer> m = lst.substringMap();
        Map<Integer, Integer> m2 = new TreeMap<Integer, Integer>(new ReverseComparator());
        for (Iterator<List> i = m.keySet().iterator(); i.hasNext(); ) {
            List l = i.next();
            Integer size = m.get(l);
            Integer found = m2.get(l.size());
            if (found == null) {
                found = size;
            } else {
                found += size;
            }
            m2.put(l.size(), found);
        }
        System.out.println("size map :" + m2);

        int remCount = 0;
        for (Iterator<Integer> i = m2.keySet().iterator(); i.hasNext(); ) {
            Integer key = i.next();
            Integer value = m2.get(key);
            m2.put(key, value - remCount);
            if (((value - remCount) < 1) || key < 2) {
                i.remove();
            }
            remCount = value;
        }

        System.out.println("new size map :" + m2);

    }

    static class ReverseComparator implements Comparator<Integer> {

        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }

    }
}
