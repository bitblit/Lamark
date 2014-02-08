/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;


/**
 * IFitnessFunction is the interface implemented by classes serving as fitness functions in Lamark.
 * <p/>
 * A fitness function takes an individual as input, and returns as output a
 * double value constituting that individual's relative "fitness".   Depending on
 * the type of problem being solved, either a minima or a maxima might be the
 * desired result.  Lamark supports this by requiring any class implementing this
 * interface to declare whether it is a minima or maxima function with the
 * fitnessType function.  Lamark has built in comparitors for sorting correctly
 * given this information.
 * <br />
 *
 * @param <T> Type of individuals in this class scores
 * @author cweiss
 * @since 4-1-06
 */
public interface IFitnessFunction<T> extends ILamarkComponent {
    /**
     * Given an individual i, calculates a fitness number for that individual.
     *
     * @param i Individual to calculate
     * @return double containing the individuals fitness score
     */
    double fitnessValue(Individual<T> i);

    /**
     * Returns the type (minima/maxima) of this fitness function.
     *
     * @return EFitnessType
     */
    EFitnessType fitnessType();
}
