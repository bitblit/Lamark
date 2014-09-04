package com.erigir.lamark.config;

import com.erigir.lamark.*;
import com.erigir.lamark.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Introspects an sourceect annotated @LamarkConfiguration and creates a Lamark instance
 * Created by chrweiss on 9/1/14.
 */
public class IntrospectLamarkFactory implements ILamarkFactory{
    private static final Logger LOG = LoggerFactory.getLogger(IntrospectLamarkFactory.class);
    private Object source;

    public IntrospectLamarkFactory() {
    }

    public IntrospectLamarkFactory(Object source) {
        this.source = source;
    }

    @Override
    public String getShortDescription() {
        return "Introspection on class "+source.getClass();
    }


    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Lamark createConfiguredLamarkInstance() {
        if (source==null)
        {
            throw new IllegalStateException("Can't introspect - source is null");
        }
        if (source.getClass().getAnnotation(LamarkConfiguration.class)==null)
        {
            throw new IllegalStateException("Can't introspect - can only introspect objects with @LamarkConfiguration");
        }
        
        Lamark rval = new Lamark();
        Map<String,Object> runtimeParameters = new TreeMap<>();
        
        Class clz = source.getClass();
        // Iterate over all the methods and find the key ones
        List<Method> paramGenerationMethods = AnnotationUtil.findMethodsByAnnotation(clz, Param.class);
        for (Method m : paramGenerationMethods) {
            Param p = m.getAnnotation(Param.class);
            if (m.getParameterTypes().length == 0) {
                LOG.debug("Calling {} for parameter {}", m, p.value());
                if (Map.class.isAssignableFrom(m.getReturnType())) {
                    runtimeParameters.putAll((Map) Util.qExec(Map.class, source, m));
                } else {
                    runtimeParameters.put(p.value(), Util.qExec(Object.class, source, m));
                }
            } else {
                throw new IllegalArgumentException("Invalid param method " + m + " - no parameters allowed");
            }
        }

        LOG.info("Configuration : {}", runtimeParameters);

        Method createMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Creator.class, true);
        rval.setCreator(new DynamicMethodWrapper<>(source, createMethod, createMethod.getAnnotation(Creator.class)));

        Method crossoverMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Crossover.class, true);
        rval.setCrossover(new DynamicMethodWrapper<>(source, crossoverMethod, crossoverMethod.getAnnotation(Crossover.class)));

        Method fitnessFunctionMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, FitnessFunction.class, true);
        rval.setFitnessFunction(new DynamicMethodWrapper(source, fitnessFunctionMethod, fitnessFunctionMethod.getAnnotation(FitnessFunction.class)));

        Method formatMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, IndividualFormatter.class, false);
        if (formatMethod == null) {
            LOG.info("No format method defined, using default");
            DefaultIndividualFormatter def = new DefaultIndividualFormatter();
            formatMethod = AnnotationUtil.findSingleMethodByAnnotation(DefaultIndividualFormatter.class, IndividualFormatter.class);
            rval.setFormatter(new DynamicMethodWrapper(def, formatMethod, formatMethod.getAnnotation(IndividualFormatter.class)));
        } else {
            rval.setFormatter(new DynamicMethodWrapper(source, formatMethod, formatMethod.getAnnotation(IndividualFormatter.class)));
        }

        Method mutateMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, Mutator.class, true);
        rval.setMutator(new DynamicMethodWrapper(source, mutateMethod, mutateMethod.getAnnotation(Mutator.class)));

        Method preloadMethod = AnnotationUtil.findSingleMethodByAnnotation(clz, PreloadIndividuals.class, false);
        if (preloadMethod != null) {
            rval.setPreloader(new DynamicMethodWrapper(source, preloadMethod, preloadMethod.getAnnotation(PreloadIndividuals.class)));
        }

        List<Method> listenerMethods = AnnotationUtil.findMethodsByAnnotation(clz, LamarkEventListener.class);
        LOG.info("Found {} listener methods", listenerMethods.size());
        for (Method m : listenerMethods) {
            rval.getListeners().add(new DynamicMethodWrapper(source, m, m.getAnnotation(LamarkEventListener.class)));
        }
        
        rval.setRuntimeParameters(runtimeParameters);
        return rval;
    }
}
