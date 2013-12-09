/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

/**
 * ICreator is the interface implemented by creators in Lamark.
 * 
 * A Creator is a class that generates a new individual.  Lamark uses
 * the creator class in 2 circumstances:
 * <ul>
 * <li>Creating the initial population for the GA</li>
 * <li>Replacing any individuals as specified by lower elitism</li>
 * </ul>
 *  
 * @author cweiss
 * @param <T> Type of individuals this class creates
 * @since 04/2007
 */
public interface ICreator<T> extends ILamarkComponent{
	
	/**
	 * Creates a new individual of the generic type specified.
	 * @return Individual object containing the new genome
	 */
	Individual<T> create();
}
