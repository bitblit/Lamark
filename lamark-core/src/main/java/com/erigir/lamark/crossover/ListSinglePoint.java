/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.crossover;

import java.util.ArrayList;
import java.util.List;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;


/**
 * A simple single-point crossover for lists (of any type).
 * NOTE: Doesn't preserve permutation property.
 * 
 * @author cweiss
 * @since 03/2005
 */
public class ListSinglePoint extends AbstractLamarkComponent implements ICrossover<List>
{

    /**
     * @see com.erigir.lamark.ICrossover#crossover(java.util.List)
     */
    public Individual<List> crossover(List<Individual<List>> parents)
    {
            List p1 = parents.get(0).getGenome();
            List p2 = parents.get(1).getGenome();
            int point = getLamark().getRandom().nextInt(p1.size());

            List<Object> c1 = new ArrayList<Object>(p1.size());

            for (int i = 0; i < point; i++)
            {
                c1.add(p1.get(i));
            }
            for (int i = point; i < p1.size(); i++)
            {
                c1.add(p2.get(i));
            }
            
            Individual i1 = new Individual(c1);

            return i1 ;
    }

    /**
     * @see com.erigir.lamark.ICrossover#parentCount()
     */
    public int parentCount()
    {
        return 2;
    }
    

}