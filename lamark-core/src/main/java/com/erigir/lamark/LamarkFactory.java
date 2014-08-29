package com.erigir.lamark;

import com.erigir.lamark.config.LamarkConfig;
import com.erigir.lamark.config.LamarkGUIConfig;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.gui.GUIEventListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
 * <p/>
 * Lamark instances hold both their configuration and data about the CURRENT run of that configuration - it is similar
 * to the relationship between a class and an instance.
 * <p/>
 * <p>
 * Since this class is responsible for loading up instances from JSON representations, it is also responsible for
 * setting the classloader before asking Jackson to read the JSON (which autoconverts class names to class objects)
 * so if a special classloader is needed (for example when reading from network or disk) it should be bundled into
 * a url classloader and set in the factory object before calling the deserialize functions
 * </p>
 * <p/>
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
    private ClassLoader classLoader;

    public LamarkFactory() {
        super();
        objectMapper = defaultObjectMapper();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public LamarkFactory(ClassLoader classLoader) {
        super();
        objectMapper = defaultObjectMapper();
        this.classLoader = classLoader;
    }

    public ObjectMapper defaultObjectMapper() {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        rval.configure(SerializationFeature.INDENT_OUTPUT, true);
        return rval;
    }

    /*
    public LamarkConfig extractConfigFromLamark(Lamark lamark) {
        LamarkConfig rval = cleanFromJSONString(convertToJson(lamark.getRuntimeParameters()), LamarkConfig.class);
        rval.setCreatorClass(lamark.getCreator().getClass());
        rval.setCreatorConfiguration(cleanFromJSONString(convertToJson(lamark.getCreator()), Map.class));
        rval.setCrossoverClass(lamark.getCrossover().getClass());
        rval.setCrossoverConfiguration(cleanFromJSONString(convertToJson(lamark.getCrossover()), Map.class));
        rval.setFitnessFunctionClass(lamark.getFitnessFunction().getClass());
        rval.setFitnessFunctionConfiguration(cleanFromJSONString(convertToJson(lamark.getFitnessFunction()), Map.class));
        rval.setMutatorClass(lamark.getMutator().getClass());
        rval.setMutatorConfiguration(cleanFromJSONString(convertToJson(lamark.getMutator()), Map.class));
        rval.setSelectorClass(lamark.getSelector().getClass());
        rval.setSelectorConfiguration(cleanFromJSONString(convertToJson(lamark.getSelector()), Map.class));
        rval.setIndividualFormatterClass(lamark.getFormatter().getClass());
        rval.setIndividualFormatterConfiguration(cleanFromJSONString(convertToJson(lamark.getFormatter()), Map.class));

        return rval;
    }*/

    public Lamark createLamarkFromConfig(LamarkConfig lc)
            throws LamarkConfigurationFailedException {
        return createLamarkFromConfig(lc, null);
    }

    public Lamark createLamarkFromConfig(LamarkConfig lc, Component optionalParentComponent)
            throws LamarkConfigurationFailedException {
        Lamark rval = new Lamark(null);

        List<String> errors = new LinkedList<String>();

        /*
        ICreator creator = factoryCreate(lc.getCreatorClass(), lc.getCreatorConfiguration());
        rval.setCreator(creator);
        require(creator, "Creator", errors);
        ICrossover crossover = factoryCreate(lc.getCrossoverClass(), lc.getCrossoverConfiguration());
        rval.setCrossover(crossover);
        require(crossover, "Crossover", errors);
        IFitnessFunction fitness = factoryCreate(lc.getFitnessFunctionClass(), lc.getFitnessFunctionConfiguration());
        rval.setFitnessFunction(fitness);
        require(fitness, "Fitness Function", errors);
        IMutator mutator = factoryCreate(lc.getMutatorClass(), lc.getMutatorConfiguration());
        rval.setMutator(mutator);
        require(mutator, "Mutator", errors);
        ISelector selector = factoryCreate(lc.getSelectorClass(), lc.getSelectorConfiguration());
        rval.setSelector(selector);
        require(selector, "Selector", errors);
        IIndividualFormatter formatter = factoryCreate(lc.getIndividualFormatterClass(), lc.getIndividualFormatterConfiguration());
        rval.setFormatter(formatter);
        require(formatter, "Individual Formatter", errors);
        */

        ExecutorService executor = null;
        if (lc.getNumberOfWorkerThreads() == null) {
            errors.add("Number of worker threads set to null (set to 1 for single-threading)");
        } else if (lc.getNumberOfWorkerThreads() < 1) {
            errors.add("Must have at least 1 worker thread");
        } else {
            executor = (lc.getNumberOfWorkerThreads() == 1) ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(lc.getNumberOfWorkerThreads());
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

        // Create any custom listeners
        List<LamarkEventListener> listeners = new LinkedList<LamarkEventListener>();
        for (Class c : lc.getCustomListeners()) {
            try {
                LamarkEventListener l = (LamarkEventListener) c.newInstance();
                if (optionalParentComponent != null && GUIEventListener.class.isAssignableFrom(c)) {
                    ((GUIEventListener) l).setParentComponent(optionalParentComponent);
                }
                listeners.add(l);
            } catch (Exception e) {
                LOG.warn("Couldn't create class of type {}", c, e);
            }
        }

        /*
        List<Individual> preloads = new LinkedList<Individual>();
        if (lc.getPreCreatedIndividuals() != null && lc.getPreCreatedIndividuals().size() > 0) {
            if (IPreloadableCreator.class.isAssignableFrom(creator.getClass())) {
                for (String s : lc.getPreCreatedIndividuals()) {
                    preloads.add(((IPreloadableCreator) creator).createFromPreload(s));
                }
            } else {
                errors.add("Preloads specified but creator doesn't implement IPreloadableCreator");
            }
        }


        if (errors.size() > 0) {
            throw new LamarkConfigurationFailedException(errors);
        } else {
            rval.setRuntimeParameters(lc);
            rval.setExecutor(executor);
            for (LamarkEventListener l : listeners) {
                rval.addGenericListener(l);
            }
            for (Individual i : preloads) {
                rval.enqueueForInsert(i);
            }

            return rval;
        }
        */
        return rval;
    }

    private void require(Object value, String name, List<String> error) {
        if (value == null) {
            error.add(name + " is required but not defined or fails to initialize");
        } else {
            conditionalValidate(value, error);
        }
    }

    public String convertToJson(Object value) {
        try {
            return (value == null) ? null : objectMapper.writeValueAsString(value);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Couldn't format", ioe);
        }
    }

    public <T> T cleanFromJSONString(String value, Class<T> clazz) {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return (value == null || clazz == null) ? null : objectMapper.readValue(value, clazz);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Couldn't format", ioe);
        }
    }

    public <T> T cleanFromJSONString(InputStream value, Class<T> clazz) {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return (value == null || clazz == null) ? null : objectMapper.readValue(value, clazz);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Couldn't format", ioe);
        }
    }

    public Map<String, LamarkGUIConfig> jsonToConfig(String json) {
        try {
            Map<String, LamarkGUIConfig> rval = null;
            if (json != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
                rval = objectMapper.readValue(json, new TypeReference<Map<String, LamarkGUIConfig>>() {
                });
            }
            return rval;
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Couldn't read json file : " + ioe.getMessage(), ioe);
        }
    }

    public Map<String, LamarkGUIConfig> jsonToConfig(InputStream json) {
        try {
            Map<String, LamarkGUIConfig> rval = null;
            if (json != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
                rval = objectMapper.readValue(json, new TypeReference<Map<String, LamarkGUIConfig>>() {
                });
            }
            return rval;
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Couldn't read json file : " + ioe.getMessage(), ioe);
        }
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private <T> T factoryCreate(Class<T> clazz, Map<String, Object> config) {
        T rval = null;
        try {
            if (clazz != null) {
                if (config == null || config.size() == 0) {
                    rval = clazz.newInstance();
                } else {
                    Thread.currentThread().setContextClassLoader(classLoader);
                    String json = objectMapper.writeValueAsString(config);
                    rval = objectMapper.readValue(json, clazz);
                }
            }
        } catch (Exception e) {
            LOG.warn("Error attempting to factory create {}", clazz, e);
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
        if (comp != null && IValidatable.class.isInstance(comp)) {
            ((IValidatable) comp).validate(errors);
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class safeLoadClass(String classname) {
        Class rval = null;
        try {
            rval = classLoader.loadClass(classname);
        } catch (ClassNotFoundException cnf) {
            LOG.warn("Couldnt find class {} - returning null", classname);
            rval = null;
        }
        return rval;
    }
}
