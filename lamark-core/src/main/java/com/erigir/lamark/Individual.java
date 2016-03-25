/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Individual is a class representing a single genome in Lamark.
 * <p/>
 * Individual is the wrapper class that holds a given genome together with its
 * fitness, and flags as to whether it was mutated or not.  Individual also contains a
 * generic "attributes" member, which the components (fitness function, creator, etc)
 * can use as a catch-all to pass information along about the individual.  This
 * feature is added primarily to cache information that is useful in various places,
 * but may be computationally expensive to perform over and over again.  Users of
 * networked Lamark should be aware that any attributes must be serializable if
 * they are to be used without error.
 * <br />
 * Note that the same genome may be present in different individuals, either because
 * they are coincidentally the same, or because they are present in different generations (ie,
 * individuals retained by upper elitism are kept forward via copies, not with multiple
 * populations pointing to the same individual.
 * <br />
 * As tracking parentage can consume a great deal of memory (it essentially keeps every
 * individual ever created through its hierarchy) it is by default turned off in Lamark.
 * Parentage information, when tracked, is kept by the Lamark object and not the individual
 * object itself.
 * <br />
 * Use of the word 'individual' for this class comes from David Goldberg's book
 *
 * @param <T> Type of objects in this class wraps
 * @author cweiss
 * @since 4-1-06
 */
public class Individual<T> implements Serializable, Comparable<Individual<T>> {
    /**
     * The actual genome of this individual, of the passed generic type
     */
    private T genome;
    /**
     * The fitness value of this individual
     */
    private Double fitness;
    /**
     * A count of how many times this genome was selected to be crossed over
     */
    private int selectedCount;
    /**
     * Tracks whether this individual contains a mutation
     */
    private boolean mutated = false;
    /**
     * Generic map of attributes for cache purposes
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * Creates a new, blank individual with a null genome.
     */
    public Individual() {
        super();
    }

    /**
     * Creates a new individual with the given genome
     *
     * @param pGenome Object containing the genome to wrap
     */
    public Individual(T pGenome) {
        super();
        this.genome = pGenome;
    }

    /**
     * Returns the class of the wrapped genome object.
     *
     * @return Class of the wrapped genome object
     */
    public Class containedType() {
        if (genome == null) {
            return null;
        }
        return genome.getClass();
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Individual <");
        sb.append(containedType());
        sb.append("> - ");
        sb.append(genome);
        if (fitness != null) {
            sb.append(" F=" + fitness);
        }
        return sb.toString();
    }

    /**
     * Fetches the attribute of the given name, or null if none exists.
     *
     * @param name String containing the name of the attribute
     * @return Object containing the attribute
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets an attribute for this individual
     *
     * @param name  String containing the name of the attribute
     * @param value Object containing the attribute itself
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Returns the number of times this individual was selected for crossover.
     * NOTE: This function is present for statistical analysis.
     *
     * @return int containing the number of times selected for crossover
     */
    public int getSelectedCount() {
        return selectedCount;
    }

    /**
     * Increments the number of times this individual was selected for crossover.
     */
    public void incrementSelected() {
        selectedCount++;
    }

    /**
     * Returns the fitness value for this individual.
     *
     * @return Double containing the fitness value, or null if not calculated yet.
     */
    public Double getFitness() {
        return fitness;
    }

    /**
     * Sets the fitness value of this individual.
     *
     * @param fitness Double containing the new fitness for this individual.
     */
    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns the genome for this individual
     *
     * @return Object containing this individuals genome.
     */
    public T getGenome() {
        return genome;
    }

    /**
     * Sets the genome for this individual
     *
     * @param value Object containing the new genome for this individual.
     */
    public void setGenome(T value) {
        this.genome = value;
    }

    /**
     * Returns the entire set of attributes for this individual.
     *
     * @return Map from string to object of attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Returns if this individual was ever mutated.
     * NOTE: This function is here for statistical analysis.
     *
     * @return true if the individual was mutated
     */
    public boolean isMutated() {
        return mutated;
    }

    /**
     * Sets whether this individual was ever mutated.
     *
     * @param mutated boolean true if the individual was mutated
     */
    public void setMutated(boolean mutated) {
        this.mutated = mutated;
    }

    @Override
    public int compareTo(Individual<T> o) {
        int rval = this.fitness.compareTo(o.fitness);
        if (rval==0)
        {
            rval = this.genome.hashCode()-o.getGenome().hashCode();
        }
        return rval;
    }
}
