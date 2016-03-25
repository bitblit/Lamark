/*
 * Created on Apr 19, 2005
 */
package com.erigir.lamark.example.schedule;

import com.erigir.lamark.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class creates a psuedorandom schedule problem and uses the DynamicScheduler
 * to calculate its total cost.  The input genome is a list of integers (a permutation)
 * which is the order in which to do the tasks.  The batching problem is in P, and
 * therefore is just calculated once the GA decides the ordering.
 *
 * @author cweiss
 * @since 04/2005
 */
public class ScheduleFitness extends AbstractLamarkComponent implements IFitnessFunction<List>, IValidatable {
    /**
     * Holds the worst time found so we can track improvement.  Really should be done by a listeners *
     */
    static int worstFound = Integer.MIN_VALUE;
    /**
     * The length of each job in units *
     */
    int[] times;
    /**
     * The relative importance of each job *
     */
    int[] weights;
    /**
     * Cache holding of all job times summed *
     */
    int totalWeightedTime;

    /**
     * Defaults the times and weights to the ones specified by assignment
     */
    public ScheduleFitness() {
        // Create the time and weight arrays
        times = new int[30];
        weights = new int[30];

        for (int i = 0; i < 30; i++) {
            times[i] = 1 + (int) Math.floor(5 * Math.sin(1.2 * i) * Math.sin(1.2 * i));
            weights[i] = (1 + i % 3);
            totalWeightedTime += times[i] * weights[i];
        }
    }

    /**
     * Makes sure that times and weights both exist, and are of the same length.
     *
     * @see com.erigir.lamark.IValidatable#validate(java.util.List)
     */
    public void validate(List<String> errors) {
        // TODO Auto-generated method stub
        if (times == null) {
            errors.add("times cannot be null");
        }
        if (weights == null) {
            errors.add("weights cannot be null");
        }
        if (times != null && weights != null && times.length != weights.length) {
            errors.add("times has " + times.length + " elements but weights has " + weights.length);
        }
    }

    public int[] getTimes() {
        return times;
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

    public int[] getWeights() {
        return weights;
    }

    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    /**
     * Converts a string (comma-delimited list) to integer array
     *
     * @param s String to convert
     * @return int[] contained in the passed string
     */
    private int[] stringToIntArr(String s) {
        String[] data = s.split(",");
        List<Integer> rval = new LinkedList<Integer>();
        for (int i = 0; i < data.length; i++) {
            rval.add(new Integer(data[i]));
        }
        int[] rd = new int[rval.size()];
        for (int i = 0; i < rval.size(); i++) {
            rd[i] = rval.get(i);
        }
        return rd;
    }

    /**
     * Converts a integer array to a comma-delimited string.
     *
     * @param val int[] to convert
     * @return String containing the array
     */
    private String intArrToString(int[] val) {
        StringBuffer sb = new StringBuffer();
        if (val != null) {
            for (int i = 0; i < val.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(val[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Reorder the source array into the permutation specified
     *
     * @param source      int[] to reorder
     * @param permutation Integer[] of the new order to use
     * @return int[] containing the reordered array
     */
    private int[] asPermutation(int[] source, Integer[] permutation) {

        getLamark().logFiner("AsPermutation, src=" + Arrays.asList(source) + " perm=" + Arrays.asList(permutation));
        if (source.length != permutation.length) {
            throw new IllegalArgumentException("Source length " + source.length + " but perm length " + permutation.length);
        }
        int[] rval = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            rval[i] = source[permutation[i].intValue()];
        }
        return rval;
    }


    /**
     * @see com.erigir.lamark.IFitnessFunction#fitnessType()
     */
    public FitnessType fitnessType() {
        return FitnessType.MINIMUM_BEST;
    }

    /**
     * Fitness function whos score is the length of the optimum schedule given this permutation.
     *
     * @see com.erigir.lamark.IFitnessFunction#fitnessValue(com.erigir.lamark.Individual)
     */
    public double fitnessValue(Individual i) {
        List<?> testList = (List<?>) i.getGenome();
        Integer[] test = testList.toArray(new Integer[0]);

        DynamicScheduler ds = new DynamicScheduler();
        ds.setup(asPermutation(times, test), asPermutation(weights, test), 1);
        int time = ds.optimalTime();
        if (time > worstFound) {
            worstFound = time;
        }
        int totalWidth = totalWeightedTime + (5 * ds.splitPoints().length);
        int[] permTime = asPermutation(times, (Integer[]) testList.toArray(new Integer[0]));
        int[] permWeight = asPermutation(weights, (Integer[]) testList.toArray(new Integer[0]));
        i.setAttribute("SCHEDULE", ds);
        i.setAttribute("WORST", worstFound);
        i.setAttribute("TOTALWIDTH", totalWidth);
        i.setAttribute("PERMTIME", permTime);
        i.setAttribute("PERMWEIGHT", permWeight);

        return time;
    }


}