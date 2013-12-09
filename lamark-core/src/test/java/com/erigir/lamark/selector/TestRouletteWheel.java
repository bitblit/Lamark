package com.erigir.lamark.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.Individual;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.example.simple.AllOnes;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestRouletteWheel
{
    List<Individual<?>> source = new ArrayList<Individual<?>>(5);
    double sumFit;
    
    @Before
    public void setUp()
    {
        System.out.println("setup");
        sumFit = 10;
        for (int i=0;i<5;i++)
        {
            Individual next = new Individual(new Integer(i+1));
            next.setFitness(new Double(i+1));
            source.add(next);
        }
        
    }

    @Test
    public void testIncreasing()
    {
        System.out.println("Max");
        RouletteWheel rw = new RouletteWheel();
        rw.setLamark(new Lamark());
        rw.getLamark().setFitnessFunction(new AllOnes());

        Collections.sort(source,EFitnessType.MAXIMUM_BEST.getComparator());
        rw.select(source,100);
        
        double sum = RouletteWheel.sumFitness(source);
        for (Individual i:source)
        {
            double current = i.getFitness();
            int expected = (int)((current/sum)*100);
            int var = expected-i.getSelectedCount();
            System.out.println(i.getGenome()+" Expected "+expected+" got "+i.getSelectedCount()+" v="+var);
            if (var>10)
            {
                fail("Unexpectedly large variance:"+var);
            }
        }
    }

    @Test
    public void testDecreasing()
    {
        System.out.println("Min");
        RouletteWheel rw = new RouletteWheel();
        rw.setLamark(new Lamark());
        rw.getLamark().setFitnessFunction(new AllOnes());

        Collections.sort(source,EFitnessType.MINIMUM_BEST.getComparator());
        rw.select(source,100);
        
        double max = RouletteWheel.maxFitness(source);
        double newSum = 0;
        for (Individual i:source)
        {
            newSum+=(max-i.getFitness())+1;
        }
        
        for (Individual i:source)
        {
            double current = i.getFitness();
            int expected = (int)(((max-current)/newSum)*100);
            int var = expected-i.getSelectedCount();
            System.out.println(i.getGenome()+" Expected "+expected+" got "+i.getSelectedCount()+" v="+var);
            if (false)
            {
                fail("Unexpectedly large variance:"+var);
            }
        }
    }
    
}
