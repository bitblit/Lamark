package com.erigir.lamark;

import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.Crossover;
import com.erigir.lamark.annotation.FitnessFunction;
import com.erigir.lamark.annotation.Mutator;
import com.erigir.lamark.builtin.StringCreator;
import com.erigir.lamark.builtin.StringCrossover;
import com.erigir.lamark.builtin.StringMutator;
import com.erigir.lamark.selector.RouletteWheel;
import com.erigir.lamark.selector.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Scans the classpath to find implementations of LamarkComponent
 * Created by chrweiss on 7/22/14.
 */
public class LamarkComponentScanner {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkComponentScanner.class);

    public static void main(String[] args) {
        try {
            LamarkComponentScanner lcs = new LamarkComponentScanner();
            LOG.info("Found components:" + lcs.findComponents("com.erigir.lamark"));


            Set components = new HashSet<>(Arrays.asList(RouletteWheel.class, Tournament.class, StringMutator.class, StringCrossover.class, StringCreator.class, StringCreator.class));
            Set<Method> creators = lcs.findCreators(components);
            Set<Method> crossovers = lcs.findCrossovers(components);
            Set<Method> mutators = lcs.findMutators(components);
            Set<Method> fitnessFunctions = lcs.findFitnessFunctions(components);

            LOG.info("Found creators: {}", creators);
            LOG.info("Found crossovers: {}", crossovers);
            LOG.info("Found mutators: {}", mutators);
            LOG.info("Found fitness: {}", fitnessFunctions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Class> findComponents(String... rootPackage)
            throws IOException {
        TreeSet<Class> rval = new TreeSet<>();


        for (String s : rootPackage) {
            LOG.info("Processing {}", s);
            ClassLoader c = ClassLoader.getSystemClassLoader();

            for (URL u : Collections.list(c.getResources(s))) {
                LOG.info("Found {} for {}", u, s);
            }


        }

        return rval;

    }

    public Set<Method> findCreators(Set<Class> lamarkComponents) {
        return findMethods(lamarkComponents, Creator.class);
    }

    public Set<Method> findFitnessFunctions(Set<Class> lamarkComponents) {
        return findMethods(lamarkComponents, FitnessFunction.class);
    }

    public Set<Method> findCrossovers(Set<Class> lamarkComponents) {
        return findMethods(lamarkComponents, Crossover.class);
    }

    public Set<Method> findMutators(Set<Class> lamarkComponents) {
        return findMethods(lamarkComponents, Mutator.class);
    }

    public <T extends Annotation> Set<Method> findMethods(Set<Class> lamarkComponents, Class<T> annotationClass) {
        HashSet<Method> rval = new HashSet<>();

        for (Class c : lamarkComponents) {
            for (Method m : c.getMethods()) {
                T creator = m.getAnnotation(annotationClass);
                if (creator != null) {
                    rval.add(m);
                }
            }
        }
        return rval;
    }

}
