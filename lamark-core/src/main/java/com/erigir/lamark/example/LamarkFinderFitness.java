package com.erigir.lamark.example;

/**
 * A fitness function that tries to find the word 'LAMARK'
 * 
 * Relies on the StringSearcher class to implement the fitness
 * function.
 * 
 * @author cweiss
 * @since 11/2007
 */
public class LamarkFinderFitness extends StringSearcher
{
    
    /**
     * Default Constructor 
     */
    public LamarkFinderFitness()
    {
        super("LAMARK");
    }
}
