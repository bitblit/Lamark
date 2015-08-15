package com.erigir.lamark.config;

import com.erigir.lamark.annotation.*;

/**
 * Created by cweiss on 7/17/15.
 */
public enum LamarkComponentType {
    CREATOR(Creator.class,false),
    CROSSOVER(Crossover.class,false),
    FITNESS_FUNCTION(FitnessFunction.class,false),
    MUTATOR(Mutator.class,false),
    SELECTOR(Selector.class,true);

    Class annotationClass;
    boolean defaultable;

    LamarkComponentType(Class annotationClass, boolean defaultable)
    {
        this.annotationClass = annotationClass;
        this.defaultable = defaultable;
    }

    public static LamarkComponentType fromAnnotationClass(Class annotationClass)
    {
        LamarkComponentType rval = null;
        LamarkComponentType[] vals = values();
        for (int i=0;i<vals.length && rval==null;i++)
        {
            if (vals[i].annotationClass.equals(annotationClass))
            {
                rval = vals[i];
            }
        }
        return rval;
    }
}
