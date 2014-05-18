package com.erigir.lamark.teacher;

import com.erigir.lamark.ICreator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.config.LamarkConfig;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

public class DynamicCreator implements ICreator {
    private Logger LOG = Logger.getLogger(DynamicCreator.class.getName());
    private static String current = defaultCode();
    private Method cacheMethod;
    private int size;

    private Lamark lamark;

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }

    public Class worksOn() {
        return Object.class;
    }


    public String translate(Individual i) {
        return "Dynamic : " + i.getGenome().toString();
    }

    private synchronized Method getMethod() {
        if (cacheMethod == null) {
            LOG.info("Fetching dynamic code from user");
            CodeDialog dialog = new CodeDialog(header(), footer(), current, "create", new Class[]{Integer.class, Properties.class, LamarkConfig.class});
            dialog.pack();
            dialog.setVisible(true);

            cacheMethod = dialog.getMethod();

            if (null == cacheMethod) {
                throw new IllegalStateException("Cannot continue, user cancelled code dialog");
            } else {
                current = dialog.getContent();
            }
        }
        return cacheMethod;
    }

    public Individual create() {
        try {
            // TODO: Call setters first?
            Object res = getMethod().invoke(null, new Object[]{});
            return new Individual(res);
        } catch (Exception e) {
            IllegalArgumentException iae = new IllegalArgumentException("Error attempting to create new individual:" + e);
            iae.initCause(e);
            throw iae;
        }
    }

    public void setSize(int newSize) {
        size = newSize;
    }

    private static String defaultCode() {
        return "";
    }

    private String header() {
        return "public static Object create(Integer size, java.util.Properties parameters, com.erigir.lamark.configure.LamarkConfig config)\n" +
                "    throws RuntimeException\n{\n" + "Object rval = null;\n";
    }

    private String footer() {
        return "return rval; \n}";
    }

}
