package com.erigir.lamark;

import java.util.Collection;

/**
 * Translates an individual object into a human-readable format.
 *
 * This function is typically taken care of by Lamark's default
 * formatter, which simply calls the toString function.
 * @see com.erigir.lamark.DefaultIndividualFormatter
 * 
 * @author cweiss
 * @since 11/02007
 * @param <T> Type of individuals in this class formats
 */
public interface IIndividualFormatter<T>
{
    /**
     * Converts the given individual to a human-readable format.
     * @param toFormat Individual to convert
     * @return String containing the human-readable version
     */
    String format(Individual<T> toFormat);
    
    /**
     * Converts a collection of individuals to a human-readable format.
     * @param toFormat Individual to convert
     * @return String containing the human-readable version
     */
    String format(Collection<Individual<T>> toFormat);
    
}
