package com.erigir.lamark.fitness;

import com.erigir.lamark.*;

import java.util.List;

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
public class StringFinderFitness extends AbstractLamarkComponent implements IFitnessFunction<String>, IValidatable {
    /**
     * The target string for lamark to search for *
     */
    private String target;

    /**
     * Default constructor
     */
    public StringFinderFitness() {
        super();
    }

    /**
     * Constructor that also defines the target string
     *
     * @param pTarget String to search for
     */
    public StringFinderFitness(String pTarget) {
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
     * Assessor for property
     *
     * @return String containing the property
     */
    public String getTarget() {
        return target;
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


}


