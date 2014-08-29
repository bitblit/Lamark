package com.erigir.lamark.config;

import com.erigir.lamark.annotation.*;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by chrweiss on 8/24/14.
 */
public class LamarkComponentFinder {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkComponentFinder.class);

    private List<String> locationsToScan;
    private Reflections reflections;

    public LamarkComponentFinder() {
        super();
        setLocationsToScan(null);
    }

    public LamarkComponentFinder(List<String> locationsToScan) {
        super();
        setLocationsToScan(locationsToScan);
    }

    public List<String> getLocationsToScan() {
        return locationsToScan;
    }

    public void setLocationsToScan(List<String> locationsToScan) {
        if (locationsToScan==null)
        {
            LOG.warn("Warning : Scanning full classpath for annotations.  Consider providing a package list to increase performance");
            locationsToScan = Arrays.asList("/");
        }
        else
        {
            this.locationsToScan = locationsToScan;
        }
        reflections = new Reflections(locationsToScan);
    }

    public Set<Class<?>> getComponentClasses() {
        return Collections.unmodifiableSet(reflections.getTypesAnnotatedWith(LamarkComponent.class));
    }

    public Set<Method> getCreators() {
        return Collections.unmodifiableSet(reflections.getMethodsAnnotatedWith(Creator.class));
    }

    public Set<Method> getCrossovers() {
        return Collections.unmodifiableSet(reflections.getMethodsAnnotatedWith(Crossover.class));
    }

    public Set<Method> getFitnessFunctions() {
        return Collections.unmodifiableSet(reflections.getMethodsAnnotatedWith(FitnessFunction.class));
    }

    public Set<Method> getFormatters() {
        return Collections.unmodifiableSet(reflections.getMethodsAnnotatedWith(IndividualFormatter.class));
    }

    public Set<Method> getMutators() {
        return Collections.unmodifiableSet(reflections.getMethodsAnnotatedWith(Mutator.class));
    }


}
