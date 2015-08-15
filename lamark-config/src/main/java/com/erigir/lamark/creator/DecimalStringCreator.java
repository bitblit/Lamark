/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

/**
 * Creator that creates individuals of type string with decimal numbers as alleles.
 *
 * @author cweiss
 * @since 09/2007
 */
public class DecimalStringCreator extends StringCreator {
    /**
     * Default constructor.
     */
    public DecimalStringCreator() {
        super();
        super.setValidCharacters("0123456789");
    }

}