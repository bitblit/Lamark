package com.erigir.lamark;

import com.erigir.lamark.annotation.Creator;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrweiss on 9/17/14.
 */
public class DynamicComponentCreator {

    /*public ICreator dynamicCreator(Object o, String id)
    {
        Class clazz = o.getClass();
        Method target = null;

        for (Method m:clazz.getMethods())
        {
            Creator create = m.getAnnotation(Creator.class);
            if (create!=null && id.equalsIgnoreCase(create.value()))
            {
                if (target!=null)
                {
                    throw new IllegalStateException("Multiple creators in class "+clazz+" found with id "+id);
                }
                target = m;
            }
        }
        if (target==null)
        {
            throw new IllegalStateException("No creator in class "+clazz+" found with id "+id);
        }



    }*/
}
