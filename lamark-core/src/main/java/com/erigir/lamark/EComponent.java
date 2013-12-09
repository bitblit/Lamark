package com.erigir.lamark;

import java.util.Properties;

/**
 * An enumeration of the various Lamark components, plus convienence methods.
 * 
 * This enumeration allows easy sharing of common functions (such as
 * configuration) across the 5 component types.  Also holds the 
 * keys used for holding information about components in a properties
 * file.
 * 
 * @author cweiss
 * @since 11/2007
 */
public enum EComponent
{
    /** Self-descriptive **/
    CREATOR,
    /** Self-descriptive **/
    CROSSOVER,
    /** Self-descriptive **/
    FITNESSFUNCTION,
    /** Self-descriptive **/
    MUTATOR,
    /** Self-descriptive **/
    SELECTOR;
    
    /** Key for the name of the creator class in Lamark.properties **/
    private static final String CREATOR_CLASS_KEY = "creatorClass";
    /** Key for the name of the crossover class in Lamark.properties **/
    private static final String CROSSOVER_CLASS_KEY = "crossoverClass";
    /** Key for the name of the fitness class in Lamark.properties **/
    private static final String FITNESS_CLASS_KEY = "fitnessClass";
    /** Key for the name of the mutator class in Lamark.properties **/
    private static final String MUTATOR_CLASS_KEY = "mutatorClass";
    /** Key for the name of the selector class in Lamark.properties **/
    private static final String SELECTOR_CLASS_KEY = "selectorClass";

    /** Key Prefix for custom properties for a creator in Lamark.properties **/
    private static final String CREATOR_PREFIX = "creator.";
    /** Key Prefix for custom properties for a crossover in Lamark.properties **/
    private static final String CROSSOVER_PREFIX = "crossover.";
    /** Key Prefix for custom properties for a fitness in Lamark.properties **/
    private static final String FITNESS_PREFIX = "fitness.";
    /** Key Prefix for custom properties for a mutator in Lamark.properties **/
    private static final String MUTATOR_PREFIX = "mutator.";
    /** Key Prefix for custom properties for a selector in Lamark.properties **/
    private static final String SELECTOR_PREFIX = "selector.";


    /**
     * Get the referenced component from the Lamark instance.
     * @param lam Lamark instance to get the component from
     * @return ILamarkComponent matching this value
     */
    public ILamarkComponent getComponent(Lamark lam)
    {
        switch (this)
        {
            case CREATOR : return lam.getCreator();
            case CROSSOVER : return lam.getCrossover();
            case FITNESSFUNCTION : return lam.getFitnessFunction();
            case MUTATOR : return lam.getMutator();
            case SELECTOR : return lam.getSelector();
            default : throw new IllegalStateException("Cant happen: invalid enum:"+this);
        }
    }
    
    /**
     * Return the property name associated with the class for this component.
     * @return String containing the property name.
     */
    public String getClassProperty()
    {
        switch (this)
        {
            case CREATOR : return CREATOR_CLASS_KEY;
            case CROSSOVER : return CROSSOVER_CLASS_KEY;
            case FITNESSFUNCTION : return FITNESS_CLASS_KEY;
            case MUTATOR : return MUTATOR_CLASS_KEY;
            case SELECTOR : return SELECTOR_CLASS_KEY;
            default : throw new IllegalStateException("Cant happen: invalid enum:"+this);
        }
    }
    
    
    /**
     * Return the property prefix associated with the custom properties for this component.
     * @return String containing the property prefix.
     */
    public String getPropertyPrefix()
    {
        switch (this)
        {
            case CREATOR : return CREATOR_PREFIX;
            case CROSSOVER : return CROSSOVER_PREFIX;
            case FITNESSFUNCTION : return FITNESS_PREFIX;
            case MUTATOR : return MUTATOR_PREFIX;
            case SELECTOR : return SELECTOR_PREFIX;
            default : throw new IllegalStateException("Cant happen: invalid enum:"+this);
        }
    }
    
    /**
     * Given a general properties object, extract custom props for this component.
     * @param p Properties object containing all properties
     * @return Properties object containing only this components properties
     */
    public Properties extractComponentProperties(Properties p)
    {
        Properties rval = new Properties();
        int preLen=getPropertyPrefix().length();
        for (Object key:p.keySet())
        {
            String s = (String)key;
            if (s.startsWith(getPropertyPrefix()))
            {
                rval.setProperty(s.substring(preLen), p.getProperty(s));
            }
        }
        return rval;
    }

}
