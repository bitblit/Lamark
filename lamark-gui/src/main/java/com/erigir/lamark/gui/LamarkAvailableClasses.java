package com.erigir.lamark.gui;

import com.erigir.lamark.LamarkBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * Classes implementing this interface serve as the source of option values in a Lamark GUI.
 *
 * Created by cweiss1271 on 3/26/16.
 */
public class LamarkAvailableClasses {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkAvailableClasses.class);

    private SortedSet<String> selectorClassNames = new TreeSet<>();
    private SortedSet<String> supplierClassNames = new TreeSet<>();
    private SortedSet<String> fitnessFunctionClassNames = new TreeSet<>();
    private SortedSet<String> crossoverClassNames = new TreeSet<>();
    private SortedSet<String> mutatorClassNames = new TreeSet<>();
    private SortedSet<String> formatterClassNames = new TreeSet<>();

    public SortedSet<String> getSelectorClassNames() {
        return selectorClassNames;
    }

    public void setSelectorClassNames(SortedSet<String> selectorClassNames) {
        this.selectorClassNames = selectorClassNames;
    }

    public SortedSet<String> getSupplierClassNames() {
        return supplierClassNames;
    }

    public void setSupplierClassNames(SortedSet<String> supplierClassNames) {
        this.supplierClassNames = supplierClassNames;
    }

    public SortedSet<String> getFitnessFunctionClassNames() {
        return fitnessFunctionClassNames;
    }

    public void setFitnessFunctionClassNames(SortedSet<String> fitnessFunctionClassNames) {
        this.fitnessFunctionClassNames = fitnessFunctionClassNames;
    }

    public SortedSet<String> getCrossoverClassNames() {
        return crossoverClassNames;
    }

    public void setCrossoverClassNames(SortedSet<String> crossoverClassNames) {
        this.crossoverClassNames = crossoverClassNames;
    }

    public SortedSet<String> getMutatorClassNames() {
        return mutatorClassNames;
    }

    public void setMutatorClassNames(SortedSet<String> mutatorClassNames) {
        this.mutatorClassNames = mutatorClassNames;
    }

    public SortedSet<String> getFormatterClassNames() {
        return formatterClassNames;
    }

    public void setFormatterClassNames(SortedSet<String> formatterClassNames) {
        this.formatterClassNames = formatterClassNames;
    }

    public void addBuilderClasses(LamarkBuilder builder)
    {
        if (builder!=null)
        {
            addClass(builder.getSelector(), selectorClassNames);
            addClass(builder.getSupplier(), supplierClassNames);
            addClass(builder.getCrossover(), crossoverClassNames);
            addClass(builder.getFitnessFunction(), fitnessFunctionClassNames);
            addClass(builder.getFormatter(), formatterClassNames);
            addClass(builder.getMutator(), mutatorClassNames);
        }
    }

    public static List<Class> toClasses(Set<String> classNames)
    {
        List<Class> rval = new ArrayList<>(classNames.size());
        for (String s:classNames)
        {
            Class c = safeLoadClass(s);
            if (c!=null)
            {
                rval.add(c);
            }
        }
        return rval;
    }

    public static Class safeLoadClass(String className)
    {
        Class rval = null;
        try
        {
            rval = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            LOG.warn("Couldn't create class {}, skipping",className);
        }
        return rval;
    }

    public static Object safeInit(Class clazz)
    {
        Object rval = null;
        if (clazz!=null) {
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                LOG.warn("Couldn't instantiate {}",clazz, e);
            }
        }
        return rval;
    }

    private void addClass(Object object, Set<String> classNames)
    {
        if (object!=null && classNames!=null)
        {
            classNames.add(object.getClass().getName());
        }
    }


}
