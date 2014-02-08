package com.erigir.lamark;

import java.util.Comparator;

/**
 * Enumerates the two types of fitness: minima and maxima.
 * Any class that implements the IFitnessFunction interface
 * must define whether is it a minimum-best function (e.g.,
 * Travelling Salesman Problem) or a maximum-best function
 * (e.g., find the string with the most ones in it).  This
 * enum enumerates them, and includes functions for
 * comparison and sorting.
 *
 * @author cweiss
 * @since 04/2007
 */
public enum EFitnessType {
    /**
     * Higher numbers are better *
     */
    MAXIMUM_BEST,
    /**
     * Lower numbers are better *
     */
    MINIMUM_BEST;

    /**
     * Get the static comparator for sorting individuals of this type.
     *
     * @return Comparator<Individual> for this individual type
     */
    public Comparator<Individual> getComparator() {
        switch (this) {
            case MAXIMUM_BEST:
                return MaximizeComparator.instance;
            case MINIMUM_BEST:
                return MinimizeComparator.instance;
            default:
                throw new IllegalStateException("Can't happen : invalid enum type " + this);
        }
    }

    /**
     * Determine whether the first individual is better than the second
     * Here "better" is defined as having either a higher or lower
     * fitness function, depending on type.  Equal values
     * will return false.
     *
     * @param i1 Individual (first) to be compared
     * @param i2 Individual (second) to be compares
     * @return true if i1 is better than i2
     */
    public boolean better(Individual i1, Individual i2) {
        switch (this) {
            case MAXIMUM_BEST:
                return i1.getFitness() > i2.getFitness();
            case MINIMUM_BEST:
                return i1.getFitness() < i2.getFitness();
            default:
                throw new IllegalStateException("Cant happen : invalid enum type");
        }
    }


    /**
     * Implements comparator to sort high fitness values to the front.
     *
     * @author cweiss
     */
    static class MaximizeComparator implements Comparator<Individual> {
        /**
         * The static instance *
         */
        public final static MaximizeComparator instance = new MaximizeComparator();

        /**
         * Private constructor to guarantee single instance *
         */
        private MaximizeComparator() {
            super();
        }

        /**
         * Compare 2 individuals where higher values are better
         *
         * @param i1 Individual (first) to be compared
         * @param i2 Individual (second) to be compared
         * @return int conforming to the comparator spec
         */
        public final int compare(Individual i1, Individual i2) {
            if (i1 == null || i2 == null) {
                throw new IllegalArgumentException("Can't compare: Null individual.. i1=" + i1 + " i2=" + i2);
            }

            Double d1 = i1.getFitness();
            Double d2 = i2.getFitness();

            if (d1 == null || d2 == null) {
                throw new IllegalArgumentException("Can't compare: Null fitness value.. i1=" + i1 + " i2=" + i2);
            }

            double d = d1 - d2;
            if (d > 0) {
                return -1;
            } else if (d < 0) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Implements comparator to sort low fitness values to the front.
     *
     * @author cweiss
     */
    static class MinimizeComparator implements Comparator<Individual> {
        /**
         * The static instance *
         */
        public static final MinimizeComparator instance = new MinimizeComparator();

        /**
         * Private constructor to guarantee single instance *
         */
        private MinimizeComparator() {
            super();
        }

        /**
         * Compare 2 individuals where lower values are better
         *
         * @param i1 Individual (first) to be compared
         * @param i2 Individual (second) to be compared
         * @return int conforming to the comparator spec
         */
        public final int compare(Individual i1, Individual i2) {
            if (i1 == null || i2 == null) {
                throw new IllegalArgumentException("Can't compare: Null individual.. i1=" + i1 + " i2=" + i2);
            }

            Double d1 = i1.getFitness();
            Double d2 = i2.getFitness();

            if (d1 == null || d2 == null) {
                throw new IllegalArgumentException("Can't compare: Null fitness value.. i1=" + i1 + " i2=" + i2);
            }

            double d = d1 - d2;
            if (d > 0) {
                return 1;
            } else if (d < 0) {
                return -1;
            }
            return 0;
        }
    }

}