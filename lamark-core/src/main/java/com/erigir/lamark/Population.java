package com.erigir.lamark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Objects of this class represent one "generation" in a
 * genetic algorithm.  Basically a thin wrapper around
 * a list of Individual objects with some statistical
 * helpers thrown in.  This also acts as the synchronizing
 * lock holder for the main thread's wait on population
 * completion.
 * <br />
 * Use of the word 'population' for this class is from David Goldberg's book.
 *
 * @param <T> Type of individuals in this population
 * @author cweiss
 * @since 04/2006
 */
public class Population<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Population.class);
    /**
     * Handle to the generating Lamark instance *
     */
    private EFitnessType fitnessType;
    //private Lamark lamark;
    /**
     * List of individuals for the generation *
     */
    private List<Individual<T>> individuals;
    /**
     * The iteration number for this generation *
     */
    private long number;
    /**
     * How big this population SHOULD be when done. *
     */
    private int targetSize;

    /**
     * Constructor of a new population object.
     * Creates a new population.  Retains a handle to the
     * lamark instance, and if the previous population
     * object is non-null, then the number of this population
     * is set to one higher than the previous population; otherwise
     * the population number is set to 0.
     *
     * @param previous Population object previous to this one in the series
     */
    public Population(EFitnessType fitnessType, int targetSize, Population<T> previous) {
        super();
        if (previous == null) {
            number = 0;
        } else {
            number = previous.getNumber() + 1;
        }
        this.fitnessType = fitnessType;
        this.targetSize = targetSize;
        this.individuals = Collections.synchronizedList(new ArrayList<Individual<T>>(targetSize));
    }

    /**
     * Throws an error if the population is still growing when it is called.
     */
    private void errOnGrowing() {
        if (individuals == null || individuals.size() < targetSize) {
            throw new IllegalStateException("Still growing");
        }
    }

    /**
     * Returns the best individual in this population.
     *
     * @return Individual that is the best
     */
    public Individual best() {
        errOnGrowing();
        return individuals.get(0);
    }

    /**
     * Gets the sublist of individuals in this population from start to finish.
     *
     * @param start
     * @param finish
     * @return List of individuals that is the requested sublist
     */
    public List<Individual<T>> getSublist(int start, int finish) {
        errOnGrowing();
        start = Math.max(0, start);
        finish = Math.min(finish, individuals.size());
        return individuals.subList(start, finish);
    }

    /**
     * Accessor method.
     *
     * @return long containing the property
     */
    public long getNumber() {
        return number;
    }

    /**
     * Adds a new individual to this population.
     * Simply adds a new individual to the list most of
     * the time.  But if this is the last individual (ie, the
     * one that raises the size to the target size), then it
     * triggers the extra steps of sorting the newly generated
     * list by fitness value, and then ending the waiting
     * of the main thread (which goes into a wait state once
     * all the work packages are queued).
     *
     * @param newIndividual Individual object to add to the population
     */
    public void addIndividual(Individual<T> newIndividual) {
        LOG.debug("Adding individual " + newIndividual);

        if (newIndividual == null) {
            throw new IllegalArgumentException("Cannot add a null new individual:" + newIndividual);
        }

        individuals.add(newIndividual);

        if (individuals.size() == targetSize) {
            // Sort the list
            Collections.sort(individuals, fitnessType.getComparator());
            endWait();
        }
    }

    /**
     * Accessor method.
     * Only valid after the population is finished building.
     *
     * @return List containing the individuals
     */
    public List<Individual<T>> getIndividuals() {
        errOnGrowing();
        return individuals;
    }

    /**
     * Returns whether the population is now complete (filled).
     *
     * @return boolean true if filled.
     */
    public boolean isFilled() {
        return getSize() == getTargetSize();
    }

    /**
     * Accessor method.
     *
     * @return int containing the property.
     */
    public int getTargetSize() {
        return targetSize;
    }

    /**
     * Returns current size of the population.
     *
     * @return int containing the size.
     */
    public synchronized int getSize() {
        if (individuals != null) {
            return individuals.size();
        } else {
            return -1;
        }
    }

    /**
     * Calculates whether the popluation is uniform (all genomes identical).
     *
     * @return boolean true if the population is uniform.
     */
    public boolean isUniform() {
        if (!isFilled()) {
            throw new IllegalStateException("Cant test for uniformity while still building");
        }
        Object o = individuals.get(0).getGenome();
        boolean deltaFound = false;
        for (int i = 1; i < individuals.size() && !deltaFound; i++) {
            deltaFound = !(individuals.get(i).getGenome().equals(o));
        }
        return !deltaFound;
    }

    /**
     * Function called for a object to go into a wait state with this object as semaphore.
     * NOTE: If the population is already filled, this function drops out
     * immediately without starting a wait.
     */
    public synchronized void startWait() {
        try {
            // double check filled
            if (!isFilled()) {
                this.wait();
            }
        } catch (InterruptedException ie) {
            throw new IllegalStateException("CANT HAPPEN, INTERRUPTED:" + ie);
        }
    }

    /**
     * Function called when the population is complete to end the wait state
     * of any threads waiting on the completion of this population.
     */
    public synchronized void endWait() {
        this.notifyAll();
    }


}
