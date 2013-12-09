/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

/**
 * Creator that creates individuals of type string with the latin alphabet as alleles.
 * 
 * @author cweiss
 * @since 09/2007
 */
public class AlphaStringCreator extends StringCreator
{
    
    /**
     * Default constructor.
     *
     */
	public AlphaStringCreator()
	{
		super();
        super.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}


}