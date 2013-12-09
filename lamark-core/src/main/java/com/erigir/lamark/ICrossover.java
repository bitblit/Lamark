/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import java.util.List;

/**
 * ICrossover is the interface implemented by Lamark crossovers.
 * 
 * Crossover is a function that takes N <em>parents</em> (by convention,
 * typically 2) and produces an offspring (more offspring can be produced by
 * repeatedly calling the function with the same parents). While the details of
 * how the crossover is implemented is of course left to the implementing class,
 * the basics of GA design stipulate that the generated child object should
 * share traits with both of the parents. 
 * 
 * In Lamark, the probability of crossover is NOT necessarily 100% 
 * (see crossoverProbability), and if the engine votes to not crossover, 
 * the first parent will instead be copied.  Therefore, the crossover
 * class doesn't have to worry about crossover probability, as this has
 * been handled prior to calling this function.
 * 
 * @author cweiss
 * @param <T> Type of individuals in this class crosses over
 * @since 4-1-07
 */
public interface ICrossover<T> extends ILamarkComponent
{
    /**
     * Given a List of individual objects, returns a child individual. The list
     * of parents will always contain the number of parents returned by the
     * parentCount function. Implementors of this interface can assume that if
     * this function is being called, then it has already been determined for
     * crossover to occur (a crossover probability failure would result in this
     * function not being called), so they can concentrate on simply
     * implementing the crossover. <br />
     * Note on parent count: Thanks to nature's influence, this is basically
     * always 2; however, Lamark will allow you to create more complicated
     * crossovers if that is your bent. <br />
     * 
     * @param parents
     *            List of individual objects to cross-over
     * @return Individual object containing the new child
     */
    Individual < T > crossover(List < Individual < T >> parents);

    /**
     * Return the number of parents this crossover expects for the crossover
     * operation.
     * 
     * @return int containing the number of parents for lamark to supply.
     */
    int parentCount();

}
