package com.erigir.lamark.config;

import com.erigir.lamark.Lamark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Scans a provided set of objects, finds and configures all lamark components
 *
 * Created by cweiss on 7/17/15.
 */
public class LamarkBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkBootstrapper.class);

    public static Lamark createLamark(List<Object> inputBeans)
    {
        Map<LamarkComponentType, List<LamarkComponent>> foundImpls = new TreeMap<>();

        LOG.info("Scanning {} beans to create a Lamark instance", inputBeans.size());
        for (Object o:inputBeans)
        {
            LOG.debug("-->: {}",o.getClass());
            for (Method m:o.getClass().getMethods())
            {
                LOG.debug("----> {}", m.getName());
                for (Annotation a:m.getAnnotations()) {
                    LamarkComponentType matchType = LamarkComponentType.fromAnnotationClass(a.annotationType());
                    if (matchType != null) {
                        LOG.debug("Adding as a valid {}", matchType);
                        List<LamarkComponent> l = foundImpls.get(matchType);
                        if (l == null) {
                            l = new LinkedList<>();
                            foundImpls.put(matchType, l);
                        }
                        l.add(new LamarkComponent(o, m, matchType));
                    }
                }
            }
        }
        LOG.info("Scan complete, attempting Lamark construction with found components : {}", foundImpls);
        Lamark rval = new Lamark();
        for (LamarkComponentType t:LamarkComponentType.values())
        {
            List<LamarkComponent> l = foundImpls.get(t);
            if ((l==null || l.size()==0) && !t.defaultable)
            {
                throw new IllegalStateException("Cannot construct: No components of type "+t+", which is not defaultable");
            }
            if (l.size()>1)
            {
                LOG.warn("Warning: found {} implementations of {} - using first one.  Specify in call to create if you wish to avoid this warning");
            }
            rval.updateComponent(l.get(0));
        }

        return rval;

    }

    public static List<LamarkComponent> extractComponentsFromObject(Object o, Class annotationClass)
    {
        List<LamarkComponent> rval = new LinkedList<>();
        LamarkComponentType findType = LamarkComponentType.fromAnnotationClass(annotationClass);
        if (findType==null)
        {
            throw new IllegalArgumentException("Cannot find matching component type from "+annotationClass);
        }
        LOG.debug("-->: {}",o.getClass());
        for (Method m:o.getClass().getMethods())
        {
            LOG.debug("----> {}", m.getName());
            for (Annotation a:m.getAnnotations()) {
                LamarkComponentType matchType = LamarkComponentType.fromAnnotationClass(a.annotationType());
                if (matchType == findType) {
                    LOG.debug("Adding as a valid {}", matchType);
                    rval.add(new LamarkComponent(o, m, matchType));
                }
            }
        }
        return rval;
    }




}
