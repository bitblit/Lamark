package com.erigir.lamark;

import com.erigir.lamark.config.LamarkConfig;
import com.erigir.lamark.config.LamarkGUIConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A class for serializing and deserializing lamark instances.
 * <br/>
 * This class translates:
 * 'Lamark Instance' to/from 'LamarkConfig Instance'
 * 'LamarkConfig Instance' to/from 'JSON representation'
 *
 * Lamark instances hold both their configuration and data about the CURRENT run of that configuration - it is similar
 * to the relationship between a class and an instance.
 *
 * <br />
 * NOTE : This is a class for simple bootstrapping.  If your auto-conf needs are more complicated then an IOC container like
 * Spring (http://www.springframework.org) is recommended.
 *
 * @author cweiss
 * @since 4-1-06
 */
public class LamarkFactory {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkFactory.class);

    private ObjectMapper objectMapper;

    public LamarkFactory()
    {
        super();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }


    public LamarkConfig extractConfigFromLamark(Lamark lamark)
    {
        LamarkConfig rval = cleanFromJSONString(cleanToJSONString(lamark.getRuntimeParameters()), LamarkConfig.class);
        rval.setCreatorClass(lamark.getCreator().getClass());
        rval.setCreatorConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getCreator()), Map.class));
        rval.setCrossoverClass(lamark.getCrossover().getClass());
        rval.setCrossoverConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getCrossover()), Map.class));
        rval.setFitnessFunctionClass(lamark.getFitnessFunction().getClass());
        rval.setFitnessFunctionConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getFitnessFunction()), Map.class));
        rval.setMutatorClass(lamark.getMutator().getClass());
        rval.setMutatorConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getMutator()), Map.class));
        rval.setSelectorClass(lamark.getSelector().getClass());
        rval.setSelectorConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getSelector()), Map.class));
        rval.setIndividualFormatterClass(lamark.getFormatter().getClass());
        rval.setIndividualFormatterConfiguration(cleanFromJSONString(cleanToJSONString(lamark.getFormatter()), Map.class));

        return rval;
    }

    public Lamark createLamarkFromConfig(LamarkConfig lc)
            throws LamarkConfigurationFailedException
    {
        List<String> errors = new LinkedList<String>();

        ICreator creator = factoryCreate(lc.getCreatorClass(), lc.getCreatorConfiguration());
        require(creator, "Creator", errors);
        ICrossover crossover = factoryCreate(lc.getCrossoverClass(), lc.getCrossoverConfiguration());
        require(crossover, "Crossover", errors);
        IFitnessFunction fitness = factoryCreate(lc.getFitnessFunctionClass(), lc.getFitnessFunctionConfiguration());
        require(fitness, "Fitness Function", errors);
        IMutator mutator = factoryCreate(lc.getMutatorClass(), lc.getMutatorConfiguration());
        require(mutator, "Mutator", errors);
        ISelector selector = factoryCreate(lc.getSelectorClass(), lc.getSelectorConfiguration());
        require(selector, "Selector", errors);
        IIndividualFormatter formatter = factoryCreate(lc.getIndividualFormatterClass(), lc.getIndividualFormatterConfiguration());
        require(formatter, "Individual Formatter", errors);

        ExecutorService executor = null;
        if (lc.getNumberOfWorkerThreads() == null) {
            errors.add("Number of worker threads set to null (set to 1 for single-threading)");
        }
        else if (lc.getNumberOfWorkerThreads() < 1) {
            errors.add("Must have at least 1 worker thread");
        }
        else
        {
            executor = (lc.getNumberOfWorkerThreads()==1)?Executors.newSingleThreadExecutor():Executors.newFixedThreadPool(lc.getNumberOfWorkerThreads());
        }

        if (lc.getPopulationSize() == null) {
            errors.add("No population size set");
        }
        if (lc.getUpperElitism() == null) {
            errors.add("Upper elitism set to null (leave at 0.0, if that's what you want)");
        }
        if (lc.getLowerElitism() == null) {
            errors.add("Lower elitism set to null (leave at 0.0, if that's what you want)");
        }
        if (lc.getCrossoverProbability() == null) {
            errors.add("Crossover probability set to null");
        }
        if (lc.getMutationProbability() == null) {
            errors.add("Mutation probability set to null (set to 0.0, if that's what you want)");
        }

        if (errors.size()>0)
        {
            throw new LamarkConfigurationFailedException(errors);
        }
        else
        {
            Lamark rval = new Lamark();
            rval.setRuntimeParameters(lc);
            rval.setCreator(creator);
            rval.setCrossover(crossover);
            rval.setFitnessFunction(fitness);
            rval.setFormatter(formatter);
            rval.setMutator(mutator);
            rval.setSelector(selector);
            rval.setExecutor(executor);

            return rval;
        }
    }

    private void require(Object value, String name, List<String> error)
    {
        if (value==null)
        {
            error.add(name+" is required but not defined or fails to initialize");
        }
        else
        {
            conditionalValidate(value, error);
        }
    }



    public String configToJson(LamarkConfig lamark)
    {
        return cleanToJSONString(lamark);
    }

    public String guiConfigToJson(LamarkGUIConfig lamark)
    {
        return cleanToJSONString(lamark);
    }

    public String cleanToJSONString(Object value)
    {
        try
        {
            return (value==null)?null:objectMapper.writeValueAsString(value);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException("Couldn't format",ioe);
        }
    }

    public <T> T cleanFromJSONString(String value, Class<T> clazz)
    {
        try
        {
            return (value==null || clazz==null)?null:objectMapper.readValue(value, clazz);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException("Couldn't format",ioe);
        }
    }

    public LamarkConfig jsonToConfig(String json)
    {
        try
        {
            return (json==null)?null:objectMapper.readValue(json, LamarkConfig.class);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException("Couldn't format",ioe);
        }
    }

    public LamarkGUIConfig jsonToGUIConfig(String json)
    {
        try
        {
            return (json==null)?null:objectMapper.readValue(json, LamarkGUIConfig.class);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException("Couldn't format",ioe);
        }
    }

    public LamarkGUIConfig jsonToGUIConfig(InputStream json)
    {
        try
        {
            return (json==null)?null:objectMapper.readValue(json, LamarkGUIConfig.class);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException("Couldn't format",ioe);
        }
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private <T> T factoryCreate(Class<T> clazz, Map<String,Object> config)
    {
        T rval = null;
        try
        {
            if (clazz!=null)
            {
                if (config==null || config.size()==0)
                {
                    rval = clazz.newInstance();
                }
                else
                {
                    String json = objectMapper.writeValueAsString(config);
                    rval = objectMapper.readValue(json, clazz);
                }
            }
        }
        catch (Exception e)
        {
            LOG.warn("Error attempting to factory create {}",clazz, e);
            rval = null;
        }

        return rval;
    }

    /**
     * Validates the given component, if it implements IValidatable.
     *
     * @param comp   Component to validate
     * @param errors List of errors, which the component can then add to if there is an error
     */
    private void conditionalValidate(Object comp, List<String> errors) {
        if (comp!=null && IValidatable.class.isInstance(comp)) {
            ((IValidatable) comp).validate(errors);
        }
    }

    /**
     * Returns an instance of the class, or null if a bad name was supplied.
     *
     * @param className String containing the name of the class to load
     * @param cl        Classloader to load that class from
     * @return Object containing the component
     */
    public static Object badClassNameToNull(String className, ClassLoader cl) {
        try {
            if (cl == null) {
                return Class.forName(className).newInstance();
            } else {
                return cl.loadClass(className).newInstance();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
