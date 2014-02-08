/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

/**
 * Creator that creates individuals of type string with zeros and ones as alleles.
 *
 * @author cweiss
 * @since 09/2007
 */
public class BinaryStringCreator extends StringCreator {
    /**
     * Default constructor.
     */
    public BinaryStringCreator() {
        super();
        super.setValidCharacters("01");
    }


}