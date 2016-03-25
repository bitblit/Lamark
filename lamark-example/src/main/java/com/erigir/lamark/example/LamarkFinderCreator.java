package com.erigir.lamark.example;

import com.erigir.lamark.creator.StringCreator;

/**
 * A class extending StringCreator that creates strings of length 6.
 * &lt;p /&gt;
 * All letters are considered valid for this string creator.
 *
 * @author cweiss
 * @since 11/2007
 */
public class LamarkFinderCreator extends StringCreator {
    /**
     * Creates the stringcreator and sets necessary parameters.
     */
    public LamarkFinderCreator() {
        super();
        super.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        super.setSize(6);
    }

}