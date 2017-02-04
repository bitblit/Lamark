package com.erigir.lamark.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkComponentDetails {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkComponentDetails.class);
    private List<Class> classes;
    private Map<String,String> config;

    public static LamarkComponentDetails createSingle(Class clazz, Map<String,String> config)
    {
        LamarkComponentDetails rval = new LamarkComponentDetails();
        rval.setClasses(Collections.singletonList(clazz));
        rval.setConfig(config);
        return rval;
    }

    @JsonIgnore
    public Class getDefault()
    {
        return classes.get(0);
    }

    public Object createConfiguredObject(int idx)
    {
        LOG.debug("Creating object of type {}",classes.get(idx));
        return createConfiguredObject(classes.get(idx), config);
    }

    public static Object createConfiguredObject(Class clazz, Map<String,String> config)
    {
        try
        {
            Object rval = clazz.newInstance();

            if (config!=null)
            {
                for (Map.Entry<String,String> e:config.entrySet())
                {
                    LOG.trace("Setting property {} to {}", e.getKey(), e.getValue());
                    String propName = e.getKey();
                    String setterName = "set"+Character.toUpperCase(propName.charAt(0))+propName.substring(1);
                    rval.getClass().getMethod(setterName, String.class).invoke(rval, e.getValue());
                }
            }

            return rval;

        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while creating configured object",e);
        }
    }

    /**
     * The default one is always the first
     * @return Object, configured with the properties
     */
    public Object createConfiguredObject()
    {
        return createConfiguredObject(0);
    }
}
