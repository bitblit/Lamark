package com.erigir.lamark.config;

import com.erigir.lamark.AnnotationUtil;
import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.Util;
import com.erigir.lamark.annotation.*;
import com.erigir.lamark.selector.ISelector;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
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
        if (locationsToScan == null) {
            LOG.warn("Warning : Scanning full classpath for annotations.  Consider providing a package list to increase performance");
            locationsToScan = Arrays.asList("/");
        } else {
            this.locationsToScan = locationsToScan;
        }
        LOG.info("About to scan {}", locationsToScan);

        /*
        ConfigurationBuilder cb = new ConfigurationBuilder();
        for (String s:locationsToScan)
        {
            cb = cb.filterInputsBy(new FilterBuilder().includePackage(s));
        }
        cb = cb.setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());


        reflections = new Reflections(cb);
        */

        reflections = new Reflections(locationsToScan);


    }

    public Map<Class,Set<Method>> findAnnotatedMethod(Class methodAnnotation) {
        HashMap<Class,Set<Method>> rval = new HashMap<>();

        for (Class c : reflections.getTypesAnnotatedWith(LamarkComponent.class)) {
            rval.put(c, ReflectionUtils.getAllMethods(c,
                    ReflectionUtils.withAnnotation(methodAnnotation)));

        }
        return rval;
    }


    public Set<Class<?>> getComponentClasses() {
        return Collections.unmodifiableSet(reflections.getTypesAnnotatedWith(LamarkComponent.class));
    }

    public Map<Class,Set<Method>> getCreators() {
        return Collections.unmodifiableMap(findAnnotatedMethod(Creator.class));
    }

    public Map<Class,Set<Method>> getCrossovers() {
        return Collections.unmodifiableMap(findAnnotatedMethod(Crossover.class));
    }

    public Map<Class,Set<Method>> getFitnessFunctions() {
        return Collections.unmodifiableMap(findAnnotatedMethod(FitnessFunction.class));
    }

    public Map<Class,Set<Method>> getFormatters() {
        return Collections.unmodifiableMap(findAnnotatedMethod(IndividualFormatter.class));
    }

    public Map<Class,Set<Method>> getMutators() {
        return Collections.unmodifiableMap(findAnnotatedMethod(Mutator.class));
    }

    public Set<Class<? extends ISelector>> getSelectors()
    {
        return reflections.getSubTypesOf(ISelector.class);
    }


    public <T> List<DynamicMethodWrapper<T>> listAsWrappers(Class<T> annotationClass)
    {
        Map<Class,Set<Method>> vals = findAnnotatedMethod(annotationClass);
        List<DynamicMethodWrapper<T>> rval = new LinkedList<>();

        for (Map.Entry<Class, Set<Method>> e:vals.entrySet())
        {
            Object holder = Util.qNewInstance(e.getKey());
            for (Method m:e.getValue())
            {
                T annotation = (T) m.getAnnotation((Class) annotationClass);
                rval.add(new DynamicMethodWrapper<T>(holder, m, annotation));
            }
        }
        return rval;
    }


}
