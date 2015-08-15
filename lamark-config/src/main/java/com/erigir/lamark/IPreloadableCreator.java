/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

/**
 * IPreloadableCreator is the interface implemented by creators in Lamark that can handle preloads.
 * <p/>
 * Same as a regular creator, but a preloadable creator can take an input string
 * and convert it to an individual.  Used to allow a json file to create a
 * set of individuals to start the system with.  How the conversion is
 * performed is problem dependant.  This interface is for use when one wants
 * a creator to be able to be bootstrapped from a json file.
 *
 * @param <T> Type of individuals in this class formats for display
 * @author cweiss
 * @since 11/2007
 */
public interface IPreloadableCreator<T> extends ICreator {

    /**
     * Creates a new individual of the generic type specified from the string.
     *
     * @param value String to convert into an individual
     * @return Individual object containing the new genome
     */
    Individual<T> createFromPreload(String value);
}
