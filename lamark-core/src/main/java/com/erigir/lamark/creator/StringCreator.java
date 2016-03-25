/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;

import java.util.*;
import java.util.function.Supplier;

/**
 * Creates new individuals of type String, with characters taken from the supplied valid character set.
 *
 * @author cweiss
 * @since 04/2006
 */
public class StringCreator extends AbstractLamarkComponent implements Supplier<String> {
    /**
     * Set of characters to create new strings from *
     */
    private List<Character> validCharacters = new ArrayList<Character>();
    /**
     * Size of the string to generate (REQUIRED)*
     */
    private int size;

    public StringCreator(Set<Character> validCharacters, int size, Random random) {
        super(random);
        initialize(validCharacters, size);
    }

    public StringCreator(Set<Character> validCharacters, int size) {
        super();
        initialize(validCharacters, size);
    }

    public StringCreator(String validCharacters, int size, Random random) {
        super(random);
        Objects.requireNonNull(validCharacters);

        Set<Character> temp = new TreeSet<>();
        for (char c:validCharacters.toCharArray())
        {
            temp.add(c);
        }

        initialize(temp, size);
    }

    private void initialize(Set<Character> validCharacters, int size)
    {
        Objects.requireNonNull(validCharacters);

        if (validCharacters.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 valid characters");
        }

        // Pass in a set for uniqueness, convert to list for random access
        this.validCharacters = new ArrayList<>(validCharacters);
        this.size = size;
    }

    public StringCreator(String validCharacters, int size) {
        super();
        Objects.requireNonNull(validCharacters);

        Set<Character> temp = new TreeSet<>();
        for (char c:validCharacters.toCharArray())
        {
            temp.add(c);
        }

        initialize(temp, size);
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

    public static StringCreator alphaAndSpaceCreator(int length, Random random)
    {
        return new StringCreator("ABCDEFGHIJKLMNOPQRSTUVWXYZ ",length,random);
    }
    public static StringCreator alphaCreator(int length, Random random)
    {
        return new StringCreator("ABCDEFGHIJKLMNOPQRSTUVWXYZ",length,random);
    }
    public static StringCreator binaryCreator(int length, Random random)
    {
        return new StringCreator("01",length,random);
    }
    public static StringCreator decimalCreator(int length, Random random)
    {
        return new StringCreator("0123456789",length,random);
    }
    public static StringCreator hexadecimalCreator(int length, Random random)
    {
        return new StringCreator("0123456789ABCDEF",length,random);
    }

    public static StringCreator alphaAndSpaceCreator(int length)
    {
        return new StringCreator("ABCDEFGHIJKLMNOPQRSTUVWXYZ ",length);
    }
    public static StringCreator alphaCreator(int length)
    {
        return new StringCreator("ABCDEFGHIJKLMNOPQRSTUVWXYZ",length);
    }
    public static StringCreator binaryCreator(int length)
    {
        return new StringCreator("01",length);
    }
    public static StringCreator decimalCreator(int length)
    {
        return new StringCreator("0123456789",length);
    }
    public static StringCreator hexadecimalCreator(int length)
    {
        return new StringCreator("0123456789ABCDEF",length);
    }

}