/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

/**
 * Creator that creates individuals of type string with the latin alphabet and space 
 * character as alleles.
 * 
 * @author cweiss
 * @since 09/2007
 */
public class AlphaAndSpaceStringCreator extends StringCreator
{
    /**
     * Default constructor.
     */
	public AlphaAndSpaceStringCreator()
	{
		super();
        super.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
	}


}