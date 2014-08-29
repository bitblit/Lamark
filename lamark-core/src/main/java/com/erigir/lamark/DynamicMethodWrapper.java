package com.erigir.lamark;

import com.erigir.lamark.annotation.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chrweiss on 8/27/14.
 */
public class DynamicMethodWrapper<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicMethodWrapper.class);
    private Object object;
    private Method method;
    private T keyAnnotation;
    private String[] parameterList;

    public DynamicMethodWrapper(Object object, Method method, T keyAnnotation) {
        if (method==null)
        {
            throw new IllegalArgumentException("Method cannot be null");
        }
        this.object = object;
        this.method = method;
        this.keyAnnotation = keyAnnotation;
        this.parameterList = buildParameterList();
    }

    public String[] getParameterList()
    {
        return parameterList;
    }

    public List<Integer> getParameterWithAnnotationIndexes(Class annotationClass)
    {
        List<Integer> rval = new LinkedList<>();
        Annotation[][] pAna = method.getParameterAnnotations();

        for (int i=0;i<pAna.length;i++)
        {
            for (Annotation a:pAna[i])
            {
                if (annotationClass.isAssignableFrom(a.getClass()))
                {
                    rval.add(i);
                }
            }
        }

        return rval;
    }

    public Object[] buildParameterArray(Map<String,Object> paramMap)
    {
        return buildParameterArray(paramMap, null);
    }

    /**
     * Builds an array of parameters from a map, filling any param gaps with the fillObject
     * @param paramMap
     * @param fillObject
     * @return
     */
    public Object[] buildParameterArray(Map<String,Object> paramMap, Object fillObject)
    {
        Object[] rval = new Object[parameterList.length];
        Class[] expTypes = method.getParameterTypes();

        for (int i=0;i<rval.length;i++)
        {
            String p = parameterList[i];
            if (p!=null)
            {
                rval[i]=paramMap.get(p);
            }
            else
            {
                rval[i]=fillObject;
            }

            // Check type
            if (rval[i]!=null)
            {
                if (!expTypes[i].isAssignableFrom(rval[i].getClass()))
                {
                    throw new IllegalStateException("Parameter "+i+" of method "+method+" should be "+expTypes[i]+" but was "+rval[i].getClass());
                }
            }

        }
        return rval;
    }

    private String[] buildParameterList()
    {
        Annotation[][] pAna = method.getParameterAnnotations();
        String[] rval = new String[pAna.length];

        for (int i=0;i<pAna.length;i++)
        {
            for (Annotation a:pAna[i])
            {
                if (Param.class.isAssignableFrom(a.getClass()))
                {
                    if (rval[i]!=null)
                    {
                        throw new IllegalStateException("Two param annotations on the same parameter is invalid");
                    }
                    Param p = (Param)a;
                    rval[i]=(p.value());
                }
            }
        }

        return rval;
    }

    public <T> T buildAndExecute(Map<String,Object> params, Class<T> clazz)
    {
        return execute(clazz, buildParameterArray(params));
    }

    public <T> T buildAndExecute(Map<String,Object> params, Object singleFill, Class<T> clazz)
    {
        return execute(clazz, buildParameterArray(params,singleFill));
    }


    public <T> T execute(Class<T> clazz, Object... args)
    {
        T rval = Util.qExec(clazz, object, method, args);
        LOG.debug("Execute on {}/{} returning {}",object, Arrays.asList(args), rval);
        return rval;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public T getKeyAnnotation() {
        return keyAnnotation;
    }

    public void setKeyAnnotation(T keyAnnotation) {
        this.keyAnnotation = keyAnnotation;
    }

    public String toString()
    {
        return "DMW [m:"+method+", ob:"+object+", an:"+keyAnnotation+"]";
    }



}
