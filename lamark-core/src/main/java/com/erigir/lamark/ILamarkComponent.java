package com.erigir.lamark;


/**
 * An interface implemented by the 5 primary components of a lamark system.
 * These primary components are enumerated in EComponent.
 * <br />
 * All of these functions are performed by classes that implement interfaces that
 * extend ILamarkComponent, and therefore must implement its functions.  It is recommended that
 * unless another class is needed to be extended, AbstractLamarkComponent be extended to simplify
 * development.  SetLamark will be guaranteed to be called before any other functions (such
 * as "create" for creators).  SetLamark is called at the point the component is set
 * into the Lamark instance.
 * 
 * @author cweiss
 * @since 4-1-06
 *
 */
public interface ILamarkComponent
{
    /**
     * Called by the lamark engine so the component will have a handle to the engine.
     * This is a lifecycle method called by the lamark engine as soon as the object is
     * set in lamark (eg, when "setCrossover" is called, the setLamark function is called
     * as part of that function.  In this way, the component will have a handle to the engine, 
     * in case the component varies its performance based on the engine or needs the engine
     * to check validation.  Using this handle a component can even
     * configure itself as a listener for lamark events, and change accordingly.
     * 
     * @param lamark Lamark instance that will use this component.
     */
    public void setLamark(Lamark lamark);
    
}
