package com.erigir.lamark.gui;

import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts classes to and from a more readable format
 * Created by cweiss1271 on 2/2/17.
 */
public class ClassComboboxStringConverter extends StringConverter<Class> {
    private static final Logger LOG = LoggerFactory.getLogger(ClassComboboxStringConverter.class);

    public static final ClassComboboxStringConverter INSTANCE = new ClassComboboxStringConverter();

    private ClassComboboxStringConverter() {
    }

    @Override
    public String toString(Class object) {
        String className = object.getName();
        if (className == null) {
            return null;
        }
        int idx = className.lastIndexOf(".");
        if (idx == -1) {
            return className;
        }
        return className.substring(idx + 1) + "[" + className.substring(0, idx) + "]";
    }

    @Override
    public Class fromString(String test) {
        // First, try reading it as a class name.  If it works, use that
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(test);
        } catch (Exception e) {
            String start = test;

            int pi = start.indexOf("[");
            String classN = start.substring(0, pi);
            String packageN = start.substring(pi + 1, start.length() - 1);
            String fullN = packageN + "." + classN;
            return safeLoadClass(fullN);
        }
    }

    public Class safeLoadClass(String className)
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

}
