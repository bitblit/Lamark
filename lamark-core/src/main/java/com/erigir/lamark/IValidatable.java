package com.erigir.lamark;

import java.util.List;

/**
 * Component classes implementing this interface wish for Lamark to validate them prior to use.
 * 
 * Other than that, they work exactly like all other ILamarkComponents.
 * 
 * @author cweiss
 * @since 9-20-07
 */
public interface IValidatable
{
    /**
     * Allows the the component to validate that it is configured correctly.
     * 
     * Any configuration errors should be added to the errors list that is 
     * passed.  Note that setLamark will be called before this function, and 
     * this function is called immediately prior to running the algorithm.
     * 
     * @param errors List<String> containing the errors with Lamark so far.
     */
    void validate(List<String> errors);
}
