/*
 * Copyright Lexikos, Inc Las Vegas NV All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of simple static functions used by Lamark.
 *
 * @author cweiss
 * @since 04/2006
 */
public class AnnotationUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationUtil.class);

    public static List<Method> findMethodsByAnnotation(Class toSearch, Class clazz)
    {
        List<Method> rval = new LinkedList<>();
        for (Method m:toSearch.getMethods())
        {
            Annotation a = m.getAnnotation(clazz);
            if (a!=null)
            {
                rval.add(m);
            }
        }
        return rval;
    }

    public static Method findSingleMethodByAnnotation(Class toSearch, Class clazz)
    {
        return findSingleMethodByAnnotation(toSearch,clazz,false);
    }

    public static Method findSingleMethodByAnnotation(Class toSearch, Class clazz, boolean exceptionOnNoneFound)
    {
        List<Method> l = findMethodsByAnnotation(toSearch,clazz);
        if (l.size()>1)
        {
            throw new IllegalStateException(l.size()+" methods found with annotation "+clazz+" but only 1 expected");
        }
        if (l.size()==0 && exceptionOnNoneFound)
        {
            throw new IllegalStateException(l.size()+" method found with annotation"+clazz+" but 1 expected");
        }

        return (l.size()==1)?l.get(0):null;
    }

}
