/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.selector;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.ISelector;
import com.erigir.lamark.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * The standardized selector for genetic algorithms, which performs
 * selection weighted according to fitness.
 *
 * @author cweiss
 * @since 04/2005
 */
public class RouletteWheel implements ISelector {
    private static final Logger LOG = LoggerFactory.getLogger(RouletteWheel.class);

    private Random random;
    private EFitnessType fitnessType;

    /**
     * Generates the sum of the fitness of a group of individuals.
     *
     * @param individuals List to sum the fitness of
     * @return double containing the sum
     */
    public static double sumFitness(List<Individual<?>> individuals) {
        double rval = 0;
        for (Individual i : individuals) {
            Double fit = i.getFitness();
            rval += fit;
        }
        return rval;
    }

    /**
     * Finds the max of the fitness of a group of individuals.
     *
     * @param individuals List to find the max fitness of
     * @return double containing the max
     */
    public static double maxFitness(List<Individual<?>> individuals) {
        double max = Double.MIN_VALUE;
        for (Individual i : individuals) {
            max = Math.max(max, i.getFitness());
        }
        return max;
    }

    /**
     * @see com.erigir.lamark.ISelector#select(java.util.List)
     */
    public Individual<?> select(List<Individual<?>> individuals) {
        double sumFit = sumFitness(individuals);

        Individual next = select(individuals, sumFit, fitnessType);
        next.incrementSelected();

        return next;
    }



    /**
     * Selects an individual, using the cached sumFitness.
     *
     * @param individuals List of individuals to select from.
     * @param sumFitness  sum of the fitness of the supplied individuals.
     * @return Individual that was selected
     */
    private Individual<?> select(List<Individual<?>> individuals, double sumFitness, EFitnessType fitnessType) {
        return individuals.get(selectIndex(extractFitness(individuals), sumFitness,random));
    }

    /**
     * Generate the index of the selected individual
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int selectIndex(List<Individual<?>> individuals, double sumFitness, EFitnessType fitnessType) {
        switch (fitnessType) {
            case MINIMUM_BEST:
                return minimizeSelectIndex(individuals, sumFitness,random);
            default:
                return maximizeSelectIndex(individuals, sumFitness,random);
        }
    }

    /**
     * Performs selection on lists that wish to minimize the end value
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int minimizeSelectIndex(List<Individual<?>> individuals, double sumFitness, Random random) {
        if (sumFitness == Double.MAX_VALUE) {
            throw new IllegalArgumentException("Cannot process if a fitness is Double.Max_Value");
        }
        double[] vals = extractFitness(individuals);
        double newSum = 0;
        double max = maxFitness(individuals);
        for (int i = 0; i < vals.length; i++) {
            vals[i] = (max - vals[i]) + 1;
            newSum += vals[i];
        }
        return selectIndex(vals, newSum,random);
    }

    /**
     * Performs selection on lists that wish to maximize the end value
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int maximizeSelectIndex(List<Individual<?>> individuals, double sumFitness, Random random) {
        return selectIndex(extractFitness(individuals), sumFitness,random);
    }

    /**
     * Given a list of individuals, gets their fitness and puts it in an array.
     *
     * @param divs List of individuals to extract fitness from.
     * @return double[] containing the fitness
     */
    private double[] extractFitness(List<Individual<?>> divs) {
        double[] rval = new double[divs.size()];
        int i = 0;
        for (Individual div : divs) {
            rval[i] = div.getFitness();
            i++;
        }
        return rval;
    }

    /**
     * Given a list of double scores and a sum, perform probabilistic selection against it.
     *
     * @param values     double[] of the fitness values
     * @param sumFitness double containing the sum of the fitness values
     * @return int containing the index of the selected value
     */
    private int selectIndex(double[] values, double sumFitness, Random random) {
        LOG.debug("selectIndex : sumFitness=" + sumFitness + " list=" + Arrays.asList(values));
        int rval = -1;

        // If sumfitness is 0, this will always return the last item
        // Therefore, if sf=0, just return random value
        if (sumFitness == 0) {
            return random.nextInt(values.length);
        }

        // Else, go ahead and calculate
        double rand = random.nextDouble() * sumFitness;
        double partSum = 0;
        LOG.debug("Rand is " + rand);

        for (int i = 0; i < values.length && rval == -1; i++) {
            partSum += values[i];
            if (partSum >= rand || i == values.length - 1) {
                rval = i;
            }
        }
        LOG.debug("sumfit=" + partSum + ", rval=" + rval);

        return rval;
    }

    public void initialize(Random random,EFitnessType fitnessType) {
        this.random = random;
        this.fitnessType = fitnessType;
    }

}