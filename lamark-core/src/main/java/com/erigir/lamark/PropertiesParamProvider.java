package com.erigir.lamark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * Created by chrweiss on 8/27/14.
 */
public class PropertiesParamProvider implements IParamProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesParamProvider.class);

    private Properties properties = new Properties();

    public PropertiesParamProvider() {
    }

    public PropertiesParamProvider(Properties properties) {
        this.properties = properties;
    }

    public PropertiesParamProvider(InputStream is) {
        try {
            this.properties.load(is);
        } catch (IOException ioe) {
            LOG.warn("Couldn't read inputstream", ioe);
            throw new RuntimeException("Failed to read property input stream", ioe);
        }
    }

    @Override
    public Object getParameter(String name) {
        return properties.get(name);
    }

    @Override
    public <T> T getParameter(String name, Class<T> clazz) {
        String prop = properties.getProperty(name);
        T rval = null;
        if (prop != null) {
            try {
                Constructor<T> cons = clazz.getConstructor(String.class);
                rval = cons.newInstance(prop);
            } catch (NoSuchMethodException nsm) {
                LOG.warn("No string-only constructor for type {} (failed to convert property {}, value {}", clazz, name, prop);
                throw new RuntimeException(nsm);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOG.warn("Failed to call constructor for type {}", clazz, e);
                throw new RuntimeException(e);
            }
        }
        return rval;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
