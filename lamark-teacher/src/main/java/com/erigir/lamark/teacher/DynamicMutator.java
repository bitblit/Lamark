package com.erigir.lamark.teacher;

import com.erigir.lamark.IMutator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Util;
import com.erigir.lamark.configure.LamarkConfig;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

public class DynamicMutator implements IMutator {
    private static String current = defaultCode();
    private Logger LOG = Logger.getLogger(DynamicCrossover.class.getName());
    private Method cacheMethod;
    private LamarkConfig config;
    private Properties properties;
    private double pMutation;


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
            CodeDialog dialog = new CodeDialog(header(), footer(), current, "mutate", new Class[]{Object.class, Properties.class, LamarkConfig.class});
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

    public boolean mutate(Individual i) {
        if (Util.flip(pMutation)) {
            try {
                getMethod().invoke(null, new Object[]{i.getGenome(), properties, config});
                return true;
            } catch (Exception e) {
                IllegalArgumentException iae = new IllegalArgumentException("Error attempting to create new individual via crossover:" + e);
                iae.initCause(e);
                throw iae;
            }
        }
        return false;
    }

    private String header() {
        return "public static void mutate(Object genome,java.util.Properties parameters, com.erigir.lamark.configure.LamarkConfig config)\n" +
                "    throws RuntimeException\n{\n";
    }

    private String footer() {
        return "\n}";
    }

    private static String defaultCode() {
        return "// NOTE: This function will only be called if mutation should take place \n" +
                "// (ie, pMutation is satisfied)\n";
    }


    public void setMutationProbability(double pPMutation) {
        pMutation = pPMutation;
    }

}
