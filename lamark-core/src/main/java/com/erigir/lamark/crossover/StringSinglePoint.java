/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import java.util.List;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;


/**
 * Simple single-point crossover for strings.
 * 
 * @author cweiss
 * @since 03/2005
 */
public class StringSinglePoint extends AbstractLamarkComponent implements ICrossover<String>
{

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    public Individual<String> crossover(List<Individual<String>> parents)
    {
            String p1 = parents.get(0).getGenome();
            String p2 = parents.get(1).getGenome();
            // len = 4, pos = 0,1,2,3  valid = 1,2
            
            if (p1==null || p2==null)
            {
                throw new IllegalArgumentException("Cant crossover, one of the parents is null");
            }
            if (p1.length()==0 || p2.length()==0)
            {
                throw new IllegalArgumentException("Cant crossover, length of one of the parents is 0");
            }
                
            
            int point = 1+getLamark().getRandom().nextInt(p1.length()-1);

            return new Individual<String>(p1.substring(0, point) + p2.substring(point));
    }

    /**
     * @see com.erigir.lamark.ICrossover#parentCount()
     */
    public int parentCount()
    {
        return 2;
    }



}