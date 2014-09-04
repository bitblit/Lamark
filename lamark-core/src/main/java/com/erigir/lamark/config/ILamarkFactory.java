package com.erigir.lamark.config;

import com.erigir.lamark.Lamark;

/**
 * Classes implementing this interface can create configured lamark instances
 *
 * Assuming you have already written any functions necessary to implement the 4 main
 * components (creator, crossover, fitness function, mutator), then the standard function of
 * an object implementing ILamarkFactory is as follows:
 * <ol>
 * <li>Create a lamark instance</li>
 * <li>Instantiate a copy of each of the 4 main components and wrap them in DynamicMethodWrappers</li>
 * <li>Set any applicable parameters on each of the components</li>
 * <li>Set the component into Lamark using the appropriate set method</li>
 * <li>Optionally set the 5th component (selector) if this is useful for you</li>
 * <li>Optionally instantiate and set a custom individual formatter</li>
 * <li>Instantiate and add any listeners as appropriate</li>
 * <li>Return the configured Lamark object</li>
 * </ol>
 *
 * Created by chrweiss on 9/1/14.
 */
public interface ILamarkFactory {
    Lamark createConfiguredLamarkInstance();
    String getShortDescription();
}
