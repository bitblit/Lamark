package com.erigir.lamark;

import java.util.Properties;

/**
 * A class implementing IConfigurable is able to configure itself via string properties.
 * 
 * This interface is designed for making a series of lamark
 * components configurable via properties files/objects.  The lamark instance
 * will pass the properties, one at a time, via the setProperty function to
 * the component, and in the case of a save, the state can be preserved by
 * calling the getProperties function.  If a class implements this interface,
 * all of its state should be able to be serialized using these 2 functions.
 * 
 * NOTE: Classes that will be used via more complex config structures (like
 * Inversion of Control) need not implement this.  It's mainly here to bootstrap
 * the GUI implementation.
 * 
 * @author cweiss
 * @since 11/07
 */
public interface IConfigurable
{
    /**
     * Lamark will call this function once per config property to pass in configuration.
     * 
     * How the configurable class chooses to implement this storage is up to the class
     * (ie, perform integer parsing, etc).  However, the class should return the appropriate
     * EConfigResult value depending on the result of the attempt to set the
     * property.  Classes should NOT assume properties will be sent in any specific order.
     * Intra-dependancies should be checked by implementing IValidatable if necessary.
     * 
     * @param name String containing the name of the property to set
     * @param value String containing the new value for the property
     * @return EConfigResult appropriate for what happens upon property set
     */
    EConfigResult setProperty(String name,String value);
    
    /**
     * Generate a properties object containing the properties necessary to reconstitute this component.
     * @return Properties object containing the properties
     */
    Properties getProperties();
}
