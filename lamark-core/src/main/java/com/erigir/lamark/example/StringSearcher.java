package com.erigir.lamark.example;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.EConfigResult;
import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.IConfigurable;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;

import java.util.List;
import java.util.Properties;

/**
 * A fitness function that searches for a given string.
 * <p/>
 * The fitness function assumes that a string is passed, and
 * its score is the ratio of correct letters to all letters.
 * When this ratio is 1.0, the target string has been found.
 *
 * @author cweiss
 * @since 10/2007
 */
public class StringSearcher extends AbstractLamarkComponent implements IFitnessFunction<String>, IValidatable, IConfigurable {
    /**
     * The target string for lamark to search for *
     */
    private String target;

    /**
     * Default constructor
     */
    public StringSearcher() {
        super();
    }

    /**
     * Constructor that also defines the target string
     *
     * @param pTarget String to search for
     */
    public StringSearcher(String pTarget) {
        super();
        target = pTarget;
    }

    /**
     * @see com.erigir.lamark.IValidatable#validate(List)
     */
    public void validate(List<String> errors) {
        if (target == null) {
            errors.add("No target string set for the fitness function");
        }
    }

    /**
     * Mutator for property
     *
     * @param pTarget Variable to change
     */
    public void setTarget(String pTarget) {
        target = pTarget.toUpperCase();
    }

    /**
     * Assessor for property
     *
     * @return String containing the property
     */
    public String getTarget() {
        return target;
    }

    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessType()
     */
    public EFitnessType fitnessType() {
        return EFitnessType.MAXIMUM_BEST;
    }

    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessValue(Individual)
     */
    public double fitnessValue(Individual<String> i) {
        String s = i.getGenome();
        double rval = 0;
        if (s.length() == target.length()) {
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == target.charAt(j)) {
                    rval++;
                }
            }
        }
        return rval / (double) s.length();
    }

    /**
     * @see com.erigir.lamark.IConfigurable#getProperties()
     */
    public Properties getProperties() {
        Properties p = new Properties();
        if (target != null) {
            p.setProperty("target", target);
        }
        return p;
    }

    /**
     * @see com.erigir.lamark.IConfigurable#setProperty(String, String)
     */
    public EConfigResult setProperty(String name, String value) {
        if (name.equalsIgnoreCase("target")) {
            target = value;
            return EConfigResult.OK;
        }
        return EConfigResult.NO_SUCH_PROPERTY;
    }


}


