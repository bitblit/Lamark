/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.supplier;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.*;
import java.util.function.Supplier;

/**
 * Creates new individuals of type String, with characters taken from the supplied valid character set.
 *
 * @author cweiss
 * @since 04/2006
 */
public class StringSupplier extends AbstractLamarkComponent implements Supplier<String> {
    /**
     * Set of characters to create new strings from *
     */
    private List<Character> validCharacters = new ArrayList<Character>();
    /**
     * Size of the string to generate (REQUIRED)*
     */
    private int size;

    public StringSupplier() {
        super();
    }

    public StringSupplier(Set<Character> validCharacters, int size, Random random) {
        super(random);
        setValidCharacters(validCharacters);
        setSize(size);
    }

    public StringSupplier(Set<Character> validCharacters, int size) {
        super();
        setValidCharacters(validCharacters);
        setSize(size);
    }

    public StringSupplier(String validCharacters, int size, Random random) {
        super(random);

        setValidCharactersByString(validCharacters);
        setSize(size);
    }

    public StringSupplier(String validCharacters, int size) {
        super();

        setValidCharactersByString(validCharacters);
        setSize(size);
    }

    /**
     * Accessor method
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }


    public String get() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(validCharacters.get(rand().nextInt(validCharacters.size())));
        }
        return sb.toString();
    }

    public void setValidCharacters(Set<Character> validCharacters) {
        Objects.requireNonNull(validCharacters);

        if (validCharacters.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 valid characters");
        }

        // Pass in a set for uniqueness, convert to list for random access
        this.validCharacters = new ArrayList<>(validCharacters);
    }

    public void setValidCharactersByString(String input)
    {
        Objects.requireNonNull(input);

        Set<Character> temp = new TreeSet<>();
        for (char c:input.toCharArray())
        {
            temp.add(c);
        }
        setValidCharacters(temp);
    }


    public void setSize(int size) {
        this.size = size;
    }
}