/*
 * Created on Feb 17, 2005
 *  
 */
package com.erigir.lamark.mutator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.IMutator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.annotation.LamarkComponent;
import com.erigir.lamark.annotation.Mutator;
import com.erigir.lamark.annotation.Param;

import java.util.Random;


/**
 * @author cweiss
 */
@LamarkComponent
public class StringSimpleMutator extends AbstractLamarkComponent implements IMutator<String> {

    /**
     * If mutation occurs, 2 random positions are switched
     *
     * @param being Individual&lt;List&gt; to be mutated
     */
    public void mutate(Individual<String> being) {

        String chromosome = being.getGenome();
        StringBuffer sb = new StringBuffer();

        int loc1 = getLamark().getRandom().nextInt(chromosome.length());
        int loc2 = getLamark().getRandom().nextInt(chromosome.length());

        while (loc1 == loc2) // make sure not the same
        {
            loc2 = getLamark().getRandom().nextInt(chromosome.length());
        }

        if (loc2 < loc1) // make sure in order
        {
            int temp = loc1;
            loc1 = loc2;
            loc2 = temp;
        }

        sb.append(chromosome.substring(0, loc1));
        sb.append(chromosome.charAt(loc2));
        sb.append(chromosome.substring(loc1 + 1, loc2));
        sb.append(chromosome.charAt(loc1));
        sb.append(chromosome.substring(loc2 + 1));
        if (sb.toString().length() != chromosome.length()) {
            throw new IllegalStateException(
                    "Mutation changed string length!");
        }
        being.setGenome(sb.toString());
    }

    @Mutator
    public String mutate(String chromosome,@Param("random")Random random)
    {

        StringBuilder sb = new StringBuilder();

        int loc1 = random.nextInt(chromosome.length());
        int loc2 = random.nextInt(chromosome.length());

        while (loc1 == loc2) // make sure not the same
        {
            loc2 = random.nextInt(chromosome.length());
        }

        if (loc2 < loc1) // make sure in order
        {
            int temp = loc1;
            loc1 = loc2;
            loc2 = temp;
        }

        sb.append(chromosome.substring(0, loc1));
        sb.append(chromosome.charAt(loc2));
        sb.append(chromosome.substring(loc1 + 1, loc2));
        sb.append(chromosome.charAt(loc1));
        sb.append(chromosome.substring(loc2 + 1));
        if (sb.toString().length() != chromosome.length()) {
            throw new IllegalStateException(
                    "Mutation changed string length!");
        }
        return sb.toString();
    }

}