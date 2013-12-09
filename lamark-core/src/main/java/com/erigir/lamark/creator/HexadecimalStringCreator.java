/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

/**
 * Creator that creates individuals of type string with hexits as alleles.
 * 
 * @author cweiss
 * @since 09/2007
 */
public class HexadecimalStringCreator extends StringCreator
{

    /**
     * Default Constructor.
     * 
     */
    public HexadecimalStringCreator()
    {
        super();
        super.setValidCharacters("0123456789ABCDEF");

    }

}