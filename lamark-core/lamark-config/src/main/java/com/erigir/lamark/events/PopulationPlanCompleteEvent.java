package com.erigir.lamark.events;

import com.erigir.lamark.StreamLamark;

/**
 * This event is fired when the system finishes its 'plan' for the next population.
 * <p>
 * That is to say, it knows how each individual will be created, but they haven't
 * been created yet.  Mainly useful for systems with VERY expensive crossover/create
 * operations that make each population take a significant amount of time to process.
 *
 * @author cweiss
 * @since 10/2007
 */
public class PopulationPlanCompleteEvent extends LamarkEvent {
    /**
     * Number of individuals that will be created by taking them from the tobeinserted queue *
     */
    private int createViaForcedInsert = 0;
    /**
     * Number of individuals that will be created by copying them via upper elitism *
     */
    private int retainViaUpperElitism = 0;
    /**
     * Number of individuals that will be created by new clean generation (lower elitism) *
     */
    private int replaceViaLowerElitism = 0;
    /**
     * Number of individuals that will be created by selection and crossover *
     */
    private int createViaCrossover = 0;


    /**
     * Default constructor
     *
     * @param pLamark                 Lamark object that generated the exception
     * @param pCreateViaForcedInsert  int containing the initial value
     * @param pRetainViaUpperElitism  int containing the initial value
     * @param pReplaceViaLowerElitism int containing the initial value
     * @param pCreateViaCrossover     int containing the initial value
     */
    public PopulationPlanCompleteEvent(StreamLamark pLamark, int pCreateViaForcedInsert, int pRetainViaUpperElitism, int pReplaceViaLowerElitism, int pCreateViaCrossover) {
        super(pLamark);

        createViaForcedInsert = pCreateViaForcedInsert;
        retainViaUpperElitism = pRetainViaUpperElitism;
        replaceViaLowerElitism = pReplaceViaLowerElitism;
        createViaCrossover = pCreateViaCrossover;

    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Population Plan: Crossover " + this.createViaCrossover +
                ", Force Insert " + this.createViaForcedInsert +
                ", Retain (Upper Elitism) " + this.retainViaUpperElitism +
                ", Replace (Lower Elitism) " + this.replaceViaLowerElitism +
                ", Total " + this.getTotal();

    }

    /**
     * Calculates the total number (should equal population size)
     *
     * @return int containing the total
     */
    public int getTotal() {
        return createViaForcedInsert + retainViaUpperElitism + replaceViaLowerElitism + createViaCrossover;
    }


    /**
     * Accessor method.
     *
     * @return int containing the property
     */
    public int getCreateViaCrossover() {
        return createViaCrossover;
    }

    /**
     * Accessor method.
     *
     * @return int containing the property
     */
    public int getCreateViaForcedInsert() {
        return createViaForcedInsert;
    }

    /**
     * Accessor method.
     *
     * @return int containing the property
     */
    public int getReplaceViaLowerElitism() {
        return replaceViaLowerElitism;
    }

    /**
     * Accessor method.
     *
     * @return int containing the property
     */
    public int getRetainViaUpperElitism() {
        return retainViaUpperElitism;
    }

}
