package com.erigir.lamark.teacher;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.config.LamarkConfig;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

public class DynamicFitness implements IFitnessFunction {
    private static String current = defaultCode();
    private Logger LOG = Logger.getLogger(DynamicFitness.class.getName());
    private Method cacheMethod;
    private LamarkConfig config;
    private Properties properties;

    private Lamark lamark;

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }


    public Class worksOn() {
        return Object.class;
    }

    public void setLamarkConfig(LamarkConfig pConfig) {
        config = pConfig;
    }

    public void configure(Properties pProperties) {
        properties = pProperties;
    }

    private synchronized Method getMethod() {
        if (cacheMethod == null) {
            LOG.info("Fetching dynamic code from user");
            CodeDialog dialog = new CodeDialog(header(), footer(), current, "fitness", new Class[]{Object.class, Properties.class, LamarkConfig.class});
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

    private String header() {
        return "public static double fitness(Object individual,java.util.Properties parameters, com.erigir.lamark.configure.LamarkConfig config)\n" +
                "    throws RuntimeException\n{\n" + "double rval = 0;\n";
    }

    private String footer() {
        return "return rval; \n}";
    }

    private static String defaultCode() {
        return "// NOTE: This function assumes that higher scores are better.\n";
    }

    public double fitnessValue(Individual i) {
        try {
            return (Double) getMethod().invoke(null, new Object[]{i.getGenome(), properties, config});
        } catch (Exception e) {
            IllegalArgumentException iae = new IllegalArgumentException("Error attempting to create new individual via crossover:" + e);
            iae.initCause(e);
            throw iae;
        }
    }

    public EFitnessType fitnessType() {
        return EFitnessType.MAXIMUM_BEST;
    }

}
