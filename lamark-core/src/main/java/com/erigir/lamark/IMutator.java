/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;


/**
 * IMutator is the interface implemented by classes serving as Lamark mutators.
 * <p/>
 * A mutator takes an individual object and changes it in some way.  It should
 * be noted that this function will only be called if the mutationProbabilty
 * has already been met, so classes implementing this function do not need to
 * perform a check, and can concentrate on the logic of the mutator.  How the
 * change is implemented is, of course, problem specific.
 *
 * @param <T> Type of individuals in this class mutates
 * @author cweiss
 * @since 4-1-07
 */
public interface IMutator<T> extends ILamarkComponent {

    /**
     * Changes the passed individual in some "random" way.
     *
     * @param i Individual to mutate.
     */
    void mutate(Individual<T> i);


}
