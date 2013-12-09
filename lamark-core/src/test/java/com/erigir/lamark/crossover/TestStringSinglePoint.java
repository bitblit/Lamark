package com.erigir.lamark.crossover;

import java.util.ArrayList;
import java.util.List;

import com.erigir.lamark.Individual;

import static org.junit.Assert.*;

import com.erigir.lamark.Lamark;
import org.junit.Test;

public class TestStringSinglePoint
{

    @Test
    public void testCrossover()
    {
        String p1="0000";
        String p2="1111";
        
        Individual<String> i1 = new Individual<String>(p1);
        Individual<String> i2 = new Individual<String>(p2);
        List<Individual<String>> parents = new ArrayList<Individual<String>>(2);
        parents.add(i1);
        parents.add(i2);
        
        StringSinglePoint ssp = new StringSinglePoint();
        ssp.setLamark(new Lamark());
        int case1=0;
        int case2=0;
        int case3=0;
        for (int i = 0;i<1000;i++)
        {
            String c1 = ssp.crossover(parents).getGenome();
            
        if (c1.equals("0111"))
        {
            case1++;
        }
        else if (c1.equals("0011"))
        {
            case2++;
        }
        else if (c1.equals("0001"))
        {
            case3++;
        }
        else
        {
            fail("Impossible child ["+c1+"]");
        }
        }
        System.out.println("Case1:"+case1+"  Case2:"+case2+"  Case3:"+case3);
    }
}
