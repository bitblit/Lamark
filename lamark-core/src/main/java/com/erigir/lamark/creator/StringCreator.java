/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.IPreloadableCreator;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates new individuals of type String, with characters taken from the supplied valid character set.
 *
 * @author cweiss
 * @since 04/2006
 */
public class StringCreator extends AbstractLamarkComponent implements IPreloadableCreator<String>, IValidatable {
    /**
     * List of characters to create new strings from *
     */
    private List<Character> validCharacters = new ArrayList<Character>();
    /**
     * Size of the list to generate (REQUIRED)*
     */
    private Integer size;

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }


    /**
     * Mutator method
     *
     * @param size new value
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @see com.erigir.lamark.IPreloadableCreator#createFromPreload(String)
     */
    public Individual<String> createFromPreload(String value) {
        if (validCharacters == null || validCharacters.size() == 0) {
            throw new IllegalStateException("Not initialized, or no valid characters");
        }
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        if (value == null) {
            throw new IllegalStateException("Cannot process, passed value was null");
        }
        if (value.length() != size) {
            throw new IllegalStateException("Cannot process, passed value was wrong size");
        }

        Individual<String> i = new Individual<String>();
        i.setGenome(value);
        return i;
    }

    /**
     * @see com.erigir.lamark.ICreator#create()
     */
    public Individual<String> create() {
        if (validCharacters == null || validCharacters.size() == 0) {
            throw new IllegalStateException("Not initialized, or no valid characters");
        }
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(validCharacters.get(getLamark().getRandom().nextInt(validCharacters
                    .size())));
        }
        Individual<String> i = new Individual<String>();
        i.setGenome(sb.toString());
        return i;
    }


    /**
     * Mutator method.
     *
     * @param validChars String containing new value
     */
    public void setValidCharacters(String validChars) {
        for (int i = 0; i < validChars.length(); i++) {
            Character c = validChars.charAt(i);
            if (!validCharacters.contains(c)) {
                validCharacters.add(c);
            }
        }
    }


    /**
     * Checks if size and validCharacters were set.
     *
     * @see com.erigir.lamark.IValidatable#validate(List)
     */
    public void validate(List<String> errors) {
        if (validCharacters == null) {
            errors.add("No 'validCharacters' set for the creator");
        }
        if (size == null) {
            errors.add("No 'size' set for the creator");
        }
    }


}