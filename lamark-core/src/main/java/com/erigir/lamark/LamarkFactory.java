package com.erigir.lamark;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * A class for creating auto-configured lamark instances from properties files.
 * 
 * This class allows Lamark instances to be created from a properties file or
 * object, which can then be run without further configuration (assuming the
 * properties file provides all necessary information, of course).  The following
 * properties are valid (see the Lamark class itself to determine which properties
 * MUST be set for a valid lamark run); all class names should be fully qualified, and
 * the classes must implement a no-arg constructor.  Look at the EComponent class for
 * the properties names for each of the classes and their custom properties keys.
 * <ul>
 * <li>maximumPopulations - Number of populations to run before stopping (note - if targetScore is set, lamark may stop earlier)</li>
 * <li>populationSize - Number of individuals in a population</li>
 * <li>upperElitism - Percent of the population to reserve across populations (ie, if set to .1 in a 20-individual population, then
 * the best 2 individuals will be copied to the next populations each cycle</li>
 * <li>lowerElitism - Percent of the population to discard each generation (ie, if set to .1 in a 20-individual population, then 2
 * individuals will be generated from scratch each population.  NOTE - this essentially functions as a massive mutation program.  Use
 * with care to avoid simply performing a random walk of the search space</li>
 * <li>crossoverProbability - Likelyhood that a crossover will occur (instead of a copy of one of the parents).  If set to 1, a crossover
 * always occurs</li>
 * <li>mutationProbability - Likelyhood that a mutation will occur <em>in a given individual</em>.  If set to 0, a mutation never occurs</li>
 * <li>individualSize - Used by many creators to determine how large a genome to make.</li>
 * <li>numberOfWorkerThreads - Number of threads to use in processing (not counting the main lamark thread)</li>
 * <li>targetScore - A score that, if reached, should cause Lamark to stop.  Lamark will stop if the current best score is >= this in the
 * case of a maxima search, or <= this in the case of a minima search</li>
 * <li>randomSeed - Value to use as the seed of the random number generator.  Used to get reproduceable runs of the GA - as long as the
 * number of worker threads is set to 1, and the runs are on machines with the same implementation of the JVM</li>
 * </ul>
 * <br />
 * NOTE : This is a class for simple bootstrapping.  If your auto-conf needs are more complicated then an IOC container like 
 * Spring (http://www.springframework.org) is recommended.
 *
 * @author cweiss
 * @since 4-1-06
 *
 */
public class LamarkFactory
{
    /** String key in properties file for upper elitism **/    
    public static final String UPPER_ELITISM_KEY = "upperElitism";
    /** String key in properties file for lower elitism **/    
    public static final String LOWER_ELITISM_KEY = "lowerElitism";
    /** String key in properties file for maximum population **/    
    public static final String MAXIMUM_POPULATION_KEY = "maximumPopulations";
    /** String key in properties file for population size **/    
    public static final String POPULATION_SIZE_KEY = "populationSize";
    /** String key in properties file for crossover probability **/    
    public static final String CROSSOVER_PROBABILITY_KEY = "crossoverProbability";
    /** String key in properties file for mutation probability **/    
    public static final String MUTATION_PROBABILITY_KEY = "mutationProbability";
    /** String key in properties file for number of worker threads **/    
    public static final String NUMBER_OF_WORKER_THREADS_KEY = "numberOfWorkerThreads";
    /** String key in properties file for target score **/    
    public static final String TARGET_SCORE_KEY = "targetScore";
    /** String key in properties file for random seed **/    
    public static final String RANDOM_SEED_KEY = "randomSeed";
    /** String key in properties file for any custom listener class names **/    
    public static final String CUSTOM_LISTENER_PREFIX="customListener.";
    /** String key in properties file for preload individuals **/    
    public static final String PRELOAD_PREFIX="preload.";

    /**
     * Creates a new lamark instance from a properties file on the resource path.
     * @param resourcePath String containing the path to the resource properties file.
     * @param cl ClassLoader to use for creation, or null for default.
     * @return Lamark object configured with those properties.
     */
    public static Lamark createFromPropertiesResource(String resourcePath,ClassLoader cl)
    {
        try
        {
            ClassLoader toRead = cl;
            if (toRead==null)
            {
                toRead = LamarkFactory.class.getClassLoader();
            }
            
            InputStream is = toRead.getResourceAsStream(resourcePath);
            Properties p = new Properties();
            p.load(is);
            return createFromProperties(p,cl);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating lamark:"+e,e);
        }
    }

    /**
     * Pass-thru function for using the default classpath
     * @param resourcePath String containing the path to the resource properties file
     * @return Lamark object configured with those properties
     */
    public static Lamark createFromPropertiesResource(String resourcePath)
    {
        return createFromPropertiesResource(resourcePath,null);
    }

    
    /**
     * Creates a new lamark instance from a properties file.
     * @param propFile File containing the properties to configure the instance with
     * @param cl Classloader to load the classes referenced 
     * @return Lamark object configured with those properties.
     */
    public static Lamark createFromPropertiesResource(File propFile,ClassLoader cl)
    {
        try
        {
            Properties p = new Properties();
            p.load(new FileInputStream(propFile));
            return createFromProperties(p,cl);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating lamark:"+e,e);
        }
    }
    
    /**
     * Pass-thru method for using the default classloader
     * @param p Properties object to create the lamark object from
     * @return Lamark object as configured by the properties
     */
    public static Lamark createFromProperties(Properties p)
    {
        return createFromProperties(p,null);
    }
    
    /**
     * Creates a new lamark instance, configured from the passed properties object.
     * @param p Properties object containing configuration
     * @param cl ClassLoader to use for creation, or null for default.
     * @return Lamark object configured with those properties
     */
    public static Lamark createFromProperties(Properties p,ClassLoader cl)
    {
        if (p==null)
        {
            throw new NullPointerException("Properties object cannot be null");
        }
        Lamark rval = new Lamark();
        initLamarkFromProperties(rval,p,cl);
        return rval;
    }

    /**
     * Initializes the passed lamark instance from the passed properties using the default classloader
     * @param lamark Lamark object to configure
     * @param p Properties object to configure the Lamark instance from
     */
    public static void initLamarkFromProperties(Lamark lamark,Properties p)
    {
        initLamarkFromProperties(lamark,p,null);
    }
    
    /**
     * Initializes the passed lamark instance from the passed properties using the provided classloader
     * @param lamark Lamark object to configure 
     * @param p Properties object to configure the Lamark instance from
     * @param cl Classloader to load the components from
     */
    public static void initLamarkFromProperties(Lamark lamark,Properties p,ClassLoader cl)
    {
        List<ConfigProperty> configProperties = new LinkedList<ConfigProperty>();
        
        for (Iterator<Object> i = p.keySet().iterator();i.hasNext();)
        {
            String key = (String)i.next();
            String value = p.getProperty(key);
            
            if (key.equals(EComponent.CREATOR.getClassProperty()))
            {
                lamark.setCreator((ICreator)loggedBadClassNameToNull(value,cl,lamark));
            }
            else if (key.equals(EComponent.CROSSOVER.getClassProperty()))
            {
                lamark.setCrossover((ICrossover)loggedBadClassNameToNull(value,cl,lamark));
            }
            else if (key.equals(EComponent.FITNESSFUNCTION.getClassProperty()))
            {
                lamark.setFitnessFunction((IFitnessFunction)loggedBadClassNameToNull(value,cl,lamark));
            }
            else if (key.equals(EComponent.MUTATOR.getClassProperty()))
            {
                lamark.setMutator((IMutator)loggedBadClassNameToNull(value,cl,lamark));
            }
            else if (key.equals(EComponent.SELECTOR.getClassProperty()))
            {
                lamark.setSelector((ISelector)loggedBadClassNameToNull(value,cl,lamark));
            }
            else if (key.equals(MAXIMUM_POPULATION_KEY))
            {
                lamark.setMaximumPopulations(toInt(value));
            }
            else if (key.equals(POPULATION_SIZE_KEY))
            {
                lamark.setPopulationSize(toInt(value));
            }
            else if (key.equals(UPPER_ELITISM_KEY))
            {
                lamark.setUpperElitism(toDbl(value));
            }
            else if (key.equals(LOWER_ELITISM_KEY))
            {
                lamark.setLowerElitism(toDbl(value));
            }
            else if (key.equals(CROSSOVER_PROBABILITY_KEY))
            {
                lamark.setCrossoverProbability(toDbl(value));
            }
            else if (key.equals(MUTATION_PROBABILITY_KEY))
            {
                lamark.setMutationProbability(toDbl(value));
            }
            else if (key.equals(NUMBER_OF_WORKER_THREADS_KEY))
            {
                lamark.setNumberOfWorkerThreads(toInt(value));
            }
            else if (key.equals(TARGET_SCORE_KEY))
            {
                lamark.setTargetScore(toDbl(value));
            }
            else if (key.equals(RANDOM_SEED_KEY))
            {
                lamark.setRandomSeed(toLong(value));
            }
            else if (key.startsWith(PRELOAD_PREFIX))
            {
                // TODO: Implement preload
            }
            else if (key.startsWith(CUSTOM_LISTENER_PREFIX))
            {
                // TODO: Custom listener
            }
            else
            {
                // Check for component properties
                for (EComponent comp:EComponent.values())
                {
                    if (key.startsWith(comp.getPropertyPrefix()))
                    {
                        configProperties.add(new ConfigProperty(comp,key.substring(comp.getPropertyPrefix().length()),value));
                    }
                }
            }
        }
        
        // Now push in all the config properties (Have to do this last)
        for (ConfigProperty cp : configProperties)
        {
            lamark.setComponentProperty(cp.target, cp.name, cp.value);
        }
    }
    
    /**
     * Returns an instance of the class, or null if a bad name was supplied.
     * This function also logs an error with lamark when this occurs
     * @param className String containing the name of the class to load
     * @param cl Classloader to load that class from
     * @param lamark Lamark object to log any errors
     * @return Object containing the component
     */
    private static Object loggedBadClassNameToNull(String className,ClassLoader cl,Lamark lamark)
    {
            Object o = badClassNameToNull(className, cl);
            if (o==null)
            {
                lamark.logWarning("Invalid class "+className);
            }
            return o;
    }
    
    /**
     * Returns an instance of the class, or null if a bad name was supplied.
     * @param className String containing the name of the class to load
     * @param cl Classloader to load that class from
     * @return Object containing the component
     */
    public static Object badClassNameToNull(String className,ClassLoader cl)
    {
        try
        {
            if (cl==null)
            {
                return Class.forName(className).newInstance();
            }
            else
            {
                return cl.loadClass(className).newInstance();
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }

    
    /**
     * Returns an integer if the string contains a integer, or null otherwise
     * @param v String to parse
     * @return Integer contained in the string
     */
    private static Integer toInt(String v)
    {
        try
        {
            return new Integer(v);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Returns a long if the string contains a long, or null otherwise
     * @param v String to parse
     * @return Long contained in the string
     */
    private static Long toLong(String v)
    {
        try
        {
            return new Long(v);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Returns a double if the string contains a double, or null otherwise
     * @param v String to parse
     * @return Double contained in the string
     */
    private static Double toDbl(String v)
    {
        try
        {
            return new Double(v);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    
    /**
     * If the passed key and value are non-null, sets them as a property in the passed Property object.
     * NOTE: Correctly handles double formatting, otherwise uses straight toString
     * @param p Properties object to receive the new property
     * @param key String containing the name of the property to set
     * @param value Object containing the value to set as a property
     */
    private static void setIfNonEmpty(Properties p,String key,Object value)
    {
        if (value!=null && p!=null && key!=null)
        {
            if (value instanceof Double)
            {
                p.setProperty(key, Util.format((Double)value));
            }
            else if (value instanceof ILamarkComponent)
            {
                p.setProperty(key, value.getClass().getName());
            }
            else
            {
                p.setProperty(key, value.toString());
            }
        }
    }

    /**
     * Creates a new properties object, from the given lamark object
     * @param l Lamark object to read properties from
     * @return Properties object containing the configuration
     */
    public static Properties lamarkToProperties(Lamark l)
    {
        if (l==null)
        {
            throw new NullPointerException("Lamark object cannot be null");
        }
        
        Properties p = new Properties();
        
        setIfNonEmpty(p,EComponent.CREATOR.getClassProperty(),l.getCreator());
        setIfNonEmpty(p,EComponent.CROSSOVER.getClassProperty(),l.getCrossover());
        setIfNonEmpty(p,EComponent.FITNESSFUNCTION.getClassProperty(),l.getFitnessFunction());
        setIfNonEmpty(p,EComponent.MUTATOR.getClassProperty(),l.getMutator());
        setIfNonEmpty(p,EComponent.SELECTOR.getClassProperty(),l.getSelector());
        setIfNonEmpty(p,LamarkFactory.UPPER_ELITISM_KEY,l.getUpperElitism());
        setIfNonEmpty(p,LamarkFactory.LOWER_ELITISM_KEY,l.getLowerElitism());
        setIfNonEmpty(p,LamarkFactory.MAXIMUM_POPULATION_KEY,l.getMaximumPopulations());
        setIfNonEmpty(p,LamarkFactory.POPULATION_SIZE_KEY,l.getPopulationSize());
        setIfNonEmpty(p,LamarkFactory.CROSSOVER_PROBABILITY_KEY,l.getCrossoverProbability());
        setIfNonEmpty(p,LamarkFactory.MUTATION_PROBABILITY_KEY,l.getMutationProbability());
        setIfNonEmpty(p,LamarkFactory.NUMBER_OF_WORKER_THREADS_KEY,l.getNumberOfWorkerThreads());
        setIfNonEmpty(p,LamarkFactory.TARGET_SCORE_KEY,l.getTargetScore());
        setIfNonEmpty(p,LamarkFactory.RANDOM_SEED_KEY,l.getRandomSeed());

        return p;
    }

    /**
     * Returns the properties file that a clean, default Lamark instance would generate.
     * @return Properties file as described.
     */
    public static Properties defaultProperties()
    {
        return lamarkToProperties(new Lamark());
    }
    
    /**
     * Thin wrapper around a custom component property.
     * 
     * Used since the class must be set before any properties can be set.
     * @author cweiss
     */
    static class ConfigProperty
    {
        /** The component this property belongs to **/
        EComponent target;
        /** The name of the property **/
        String name;
        /** The value of the property **/
        String value;
        
        /**
         * Default constructor
         * @param target EComponent type to receive the property
         * @param name String name of the property
         * @param value String value of the property
         */
        public ConfigProperty(EComponent target, String name, String value)
        {
            super();
            this.target = target;
            this.name = name;
            this.value = value;
        }
        
    }

}
