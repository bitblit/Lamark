package com.erigir.lamark.teacher;

import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.config.LamarkConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DynamicCrossover implements ICrossover<T> {
    private static String current = defaultCode();
    private Logger LOG = Logger.getLogger(DynamicCrossover.class.getName());
    private Method cacheMethod;
    private LamarkConfig config;
    private Properties properties;
    private double pCrossover;

    private Lamark lamark;

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }

    private synchronized Method getMethod() {
        if (cacheMethod == null) {
            LOG.info("Fetching dynamic code from user");
            CodeDialog dialog = new CodeDialog(header(), footer(), current, "crossover", new Class[]{Object[].class, Properties.class, LamarkConfig.class});
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

    @Override
    public Individual<T> crossover(List<Individual<T>> parents) {

        try {

            T temp = null;
            for (int i = 0; i < 2; i++) {
                temp = parents.get(i).getGenome();
            }

            // TODO: call setters first?
            Object[] res = (Object[]) getMethod().invoke(null, new Object[]{});
            return new Individual(res);
        } catch (Exception e) {
            IllegalArgumentException iae = new IllegalArgumentException("Error attempting to create new individual via crossover:" + e);
            iae.initCause(e);
            throw iae;
        }
    }

    private String header() {
        return "public static Object[] crossover(Object[] parents,java.util.Properties parameters, com.erigir.lamark.configure.LamarkConfig config)\n" +
                "    throws RuntimeException\n{\n" + "Object[] rval = new Object[2];\n";
    }

    private String footer() {
        return "return rval; \n}";
    }

    private static String defaultCode() {
        return "// NOTE: The 'parents' array above will have 2 elements.  The rval array also has 2 and\n" +
                "// should be of the same type.  This function will only be called if the crossover will\n" +
                "// actually take place for these two parents (ie, pCrossover is satisfied)\n";
    }

    public int parentCount() {
        return 2; // NOTE : this is fixed since the dynamic one is just for teaching, easily fixed later
    }

    public int childCount() {
        return 2; // NOTE : this is fixed since the dynamic one is just for teaching, easily fixed later
    }

    public void setCrossoverProbability(double pPCrossover) {
        pCrossover = pPCrossover;
    }

}
