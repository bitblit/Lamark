package com.erigir.lamark.events;

import com.erigir.lamark.EConfigResult;
import com.erigir.lamark.IConfigurable;
import com.erigir.lamark.Lamark;

/**
 * An event fired every time one of the configurable objects is sent a property.
 *
 * @author cweiss
 * @since 11/2007
 */
public class ConfigurationEvent extends LamarkEvent {

    /**
     * Configurable component that got the set called
     */
    private IConfigurable confObject;
    /**
     * Name of the property lamark tried to set *
     */
    private String name;
    /**
     * Value of the property lamark tried to set *
     */
    private String value;
    /**
     * What happened when lamark tried to set the property *
     */
    private EConfigResult result;

    /**
     * Default constructor
     *
     * @param pLamark    Lamark object that generated the exception
     * @param confObject IConfigurable that was to receive the property
     * @param name       String containing the name of the property
     * @param value      String containing the value of the property
     * @param result     EConfigResult wrapping up what happened when set was called
     */
    public ConfigurationEvent(Lamark pLamark, IConfigurable confObject, String name, String value, EConfigResult result) {
        super(pLamark);
        this.confObject = confObject;
        this.name = name;
        this.value = value;
        this.result = result;
    }

    /**
     * Get handle to the target configurable object
     *
     * @return IConfigurableObject that was modified
     */
    public IConfigurable getConfObject() {
        return confObject;
    }

    /**
     * Get name of the property to set
     *
     * @return String containing the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get result of setting the property
     *
     * @return EConfigResult wrapping what happened at property set time
     */
    public EConfigResult getResult() {
        return result;
    }

    /**
     * Get value lamark tried to set the property to
     *
     * @return String containing the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ConfigurationEvent:" + result.toString() + " setting '" + name + "' to '" + value + "' on " + confObject;
    }
}
