/*
 * Created on Feb 17, 2005
 *  
 */
package com.erigir.lamark.mutator;

import java.util.ArrayList;
import java.util.List;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.IMutator;
import com.erigir.lamark.Individual;


/**
 * Simple, permutation safe 2 point array switch.
 * Picks 2 points and exchanges them.  Does not exercise any
 * unused portions of the input alphabet.
 * 
 * @author cweiss
 * @since 04/2005
 *  
 */
public class ListSimpleMutator extends AbstractLamarkComponent implements IMutator<List>
{
    /**
	 * @see com.erigir.lamark.IMutator#mutate(com.erigir.lamark.Individual)
	 */
	public void mutate(Individual<List> being)
	{
            List tempChromosome = being.getGenome();
            List<Object> chromosome = new ArrayList<Object>(tempChromosome.size());
            chromosome.addAll(tempChromosome);
            int loc1 = getLamark().getRandom().nextInt(chromosome.size());
            int loc2 = getLamark().getRandom().nextInt(chromosome.size());
            Object temp = chromosome.get(loc2);
            chromosome.set(loc2,chromosome.get(loc1));
            chromosome.set(loc1,temp);
            being.setGenome(chromosome);
	}
    

}