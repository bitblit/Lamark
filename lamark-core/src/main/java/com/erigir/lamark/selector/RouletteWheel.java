/*
 * Created on Feb 17, 2005
 */
package com.erigir.lamark.selector;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.ISelector;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.annotation.LamarkComponent;
import com.erigir.lamark.annotation.Selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The standardized selector for genetic algorithms, which performs
 * selection weighted according to fitness.
 *
 * @author cweiss
 * @since 04/2005
 */
@LamarkComponent
public class RouletteWheel extends AbstractLamarkComponent implements ISelector {
    /**
     * Default constructor.
     */
    public RouletteWheel() {
        super();
    }

    /**
     * Constructor that accepts handle to creating lamark instance.
     *
     * @param lamark Lamark instance containing this selector
     */
    public RouletteWheel(Lamark lamark) {
        super();
        setLamark(lamark);
    }

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
     * @see com.erigir.lamark.ISelector#select(java.util.List, int)
     */
    @Selector
    public List<Individual<?>> select(List<Individual<?>> individuals, int count) {
        double sumFit = sumFitness(individuals);
        List<Individual<?>> rval = new ArrayList<Individual<?>>(count);
        for (int i = 0; i < count; i++) {
            Individual next = select(individuals, sumFit);
            next.incrementSelected();
            rval.add(next);
        }
        return rval;

    }



    /**
     * Selects an individual, using the cached sumFitness.
     *
     * @param individuals List of individuals to select from.
     * @param sumFitness  sum of the fitness of the supplied individuals.
     * @return Individual that was selected
     */
    private Individual<?> select(List<Individual<?>> individuals, double sumFitness) {
        return individuals.get(selectIndex(individuals, sumFitness));
    }

    /**
     * Generate the index of the selected individual
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int selectIndex(List<Individual<?>> individuals, double sumFitness) {
        switch (getLamark().getFitnessFunction().fitnessType()) {
            case MINIMUM_BEST:
                return minimizeSelectIndex(individuals, sumFitness);
            default:
                return maximizeSelectIndex(individuals, sumFitness);
        }
    }

    /**
     * Performs selection on lists that wish to minimize the end value
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int minimizeSelectIndex(List<Individual<?>> individuals, double sumFitness) {
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
        return selectIndex(vals, newSum);
    }

    /**
     * Performs selection on lists that wish to maximize the end value
     *
     * @param individuals List of individuals to select from
     * @param sumFitness  double containing the sum of the lists fitness values
     * @return int containing the index of the selected value
     */
    private int maximizeSelectIndex(List<Individual<?>> individuals, double sumFitness) {
        return selectIndex(extractFitness(individuals), sumFitness);
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
    private int selectIndex(double[] values, double sumFitness) {
        getLamark().logFiner("selectIndex : sumFitness=" + sumFitness + " list=" + Arrays.asList(values));
        int rval = -1;

        // If sumfitness is 0, this will always return the last item
        // Therefore, if sf=0, just return random value
        if (sumFitness == 0) {
            return getLamark().getRandom().nextInt(values.length);
        }

        // Else, go ahead and calculate
        double rand = getLamark().getRandom().nextDouble() * sumFitness;
        double partSum = 0;
        getLamark().logFine("Rand is " + rand);

        for (int i = 0; i < values.length && rval == -1; i++) {
            partSum += values[i];
            if (partSum >= rand || i == values.length - 1) {
                rval = i;
            }
        }
        getLamark().logFiner("sumfit=" + partSum + ", rval=" + rval);

        return rval;
    }


}