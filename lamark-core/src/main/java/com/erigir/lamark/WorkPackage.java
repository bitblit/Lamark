package com.erigir.lamark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A WorkPackage represents a "work order" for new individual instantiation.
 * <p/>
 * A work package can create a new instance via creation, crossover, or copying.  The
 * work package is also responsible for calculating the fitness value of an individual,
 * and for placing the newly created individual into the growing population object.
 * This class is here to simplify the work of multithreading, by allowing the various
 * threads to simply take on the work of executing the WorkPackage.  The
 * overall processor knows a population is finished when a call to "addIndividual"
 * pushes its size to the limit.
 * <br />
 * In addition, in order to avoid the overhead of creating/destroying wp
 * objects over and over, a pool of them is maintained and handed out
 * each time.
 *
 * @author cweiss
 * @since 09/2007
 */
public class WorkPackage implements Runnable {

    /**
     * Static cached list of workpackage objects held to prevent repeated instantiation *
     */
    private static final List<WorkPackage> AVAILABLE_WP = Collections.synchronizedList(new LinkedList<WorkPackage>());
    /**
     * Enum holding what type of workpackage this is (create/crossover, etc) *
     */
    private Type type;
    /**
     * Handle to the creating lamark instance *
     */
    private Lamark lamark;
    /**
     * Handle to a individual to copy, if needed *
     */
    private Individual<?> toCopy;
    /**
     * Population into which to put the new individual*
     */
    private Population targetPopulation;
    /**
     * Population from which to select individuals for crossover *
     */
    private Population crossoverSourcePopulation;

    /**
     * Private constructor to prevent non-factory instantiation.
     */
    private WorkPackage() {
        super();
    }

    /**
     * Generate the size of the current WP cache list.
     *
     * @return int containing the size
     */
    public static final int queueSize() {
        return AVAILABLE_WP.size();
    }

    /**
     * Auto-grows the queue size to be at least the given size
     *
     * @param size int containing the size to which to grow the work package cached queue.
     */
    public static void initializeQueue(int size) {
        while (AVAILABLE_WP.size() < size) {
            AVAILABLE_WP.add(new WorkPackage());
        }
    }

    /**
     * Factory method to create a new "create" type work package (used by the public factory methods).
     *
     * @param lamark    Lamark instance to create the individual for
     * @param sourcePop Population that was previous to this one
     * @param targetPop Population to put the new individual into
     * @param toCopy    Individual to copy if necessary
     * @param type      Type of workapckage to create
     * @return WorkPackage object created by the factory method
     */
    private static WorkPackage create(Lamark lamark, Population sourcePop, Population targetPop, Individual<?> toCopy, Type type) {
        WorkPackage rval = null;
        if (!AVAILABLE_WP.isEmpty()) {
            rval = AVAILABLE_WP.remove(0);
            lamark.logFine("Reusing workpackage object, " + AVAILABLE_WP.size() + " remaining");
        } else {
            lamark.logFine("Creating new workpackage object, ");
            rval = new WorkPackage();
        }

        rval = new WorkPackage();

        rval.lamark = lamark;
        rval.targetPopulation = targetPop;
        rval.type = type;
        rval.toCopy = toCopy;
        rval.crossoverSourcePopulation = sourcePop;

        return rval;
    }

    /**
     * Factory method to create a set of workpackages (all of type CREATE).
     *
     * @param lamark    Lamark object to create the WP's for
     * @param targetPop Population to add the created individuals to.
     * @param count     int containing the number of individuals to create
     * @return List of workpackages to perform the work
     */
    public static List<WorkPackage> newItems(Lamark lamark, Population targetPop, int count) {
        List<WorkPackage> rval = new ArrayList<WorkPackage>(count);
        for (int i = 0; i < count; i++) {
            rval.add(newItem(lamark, targetPop));
        }
        return rval;
    }

    /**
     * Factory method to create a set of workpackages (all of type CROSSOVER).
     *
     * @param lamark    Lamark object to create the WP's for
     * @param sourcePop Population to select inidividuals for crossover from
     * @param targetPop Population to add the created individuals to.
     * @param count     int containing the number of individuals to create
     * @return List of workpackages to perform the work
     */
    public static List<WorkPackage> crossovers(Lamark lamark, Population sourcePop, Population targetPop, int count) {
        List<WorkPackage> rval = new ArrayList<WorkPackage>(count);
        for (int i = 0; i < count; i++) {
            rval.add(crossover(lamark, sourcePop, targetPop));
        }
        return rval;
    }

    /**
     * Factory method to create a set of workpackages (all of type COPY).
     *
     * @param lamark     Lamark object to create the WP's for
     * @param targetPop  Population to add the created individuals to.
     * @param toBeCopied List of individuals to copy
     * @return List of workpackages to perform the work
     */
    public static List<WorkPackage> copies(Lamark lamark, Population targetPop, List<Individual<?>> toBeCopied) {
        int count = toBeCopied.size();
        List<WorkPackage> rval = new ArrayList<WorkPackage>(count);
        for (Individual<?> div : toBeCopied) {
            rval.add(copy(lamark, div, targetPop));
        }

        return rval;
    }

    /**
     * Factory method to create a new item via create
     *
     * @param lamark    Lamark object to create the WP's for
     * @param targetPop Population to add the created individuals to.
     * @return Workpackage to perform the work
     */
    public static WorkPackage newItem(Lamark lamark, Population targetPop) {
        return create(lamark, null, targetPop, null, Type.NEW);
    }

    /**
     * Factory method to create a new item via copy
     *
     * @param lamark    Lamark object to create the WP's for
     * @param i         Individual to copy
     * @param targetPop Population to add the created individuals to.
     * @return Workpackage to perform the work
     */
    public static WorkPackage copy(Lamark lamark, Individual i, Population targetPop) {
        return create(lamark, null, targetPop, i, Type.COPY);
    }

    /**
     * Factory method to create a new item via crossover
     *
     * @param sourcePop Population to select inidividuals for crossover from
     * @param lamark    Lamark object to create the WP's for
     * @param targetPop Population to add the created individuals to.
     * @return Workpackage to perform the work
     */
    public static WorkPackage crossover(Lamark lamark, Population sourcePop, Population targetPop) {
        return create(lamark, sourcePop, targetPop, null, Type.CROSSOVER);
    }

    /**
     * Method called by the running thread (typically from an execution pool) to perform the work.
     */
    public void run() {
        try {
            Individual rval;
            lamark.logFine("WP : " + hashCode() + " processed by thread " + Thread.currentThread());

            // First, create
            switch (type) {
                case NEW:
                    rval = lamark.getCreator().create();
                    break;
                case COPY:
                    rval = new Individual(toCopy.getGenome());
                    break;
                case CROSSOVER:
                    int parCount = lamark.getCrossover().parentCount();
                    List<Individual<?>> source = crossoverSourcePopulation.getIndividuals();
                    List<Individual<?>> parents = lamark.getSelector().select(source, parCount);
                    if (lamark.crossoverFlip()) {
                        // Create the individual
                        rval = lamark.getCrossover().crossover(parents);
                        // Possibly register the parentage
                        lamark.registerParentage(rval, parents);
                    } else {
                        rval = new Individual(parents.get(0));
                    }
                    break;
                default: {
                    throw new IllegalStateException("Invalid create type:" + type);
                }
            }

            // Now, potentially mutate
            if (lamark.mutationFlip()) {
                lamark.getMutator().mutate(rval);
                rval.setMutated(true);
            }

            // Now, calc the fitness value
            rval.setFitness(lamark.getFitnessFunction().fitnessValue(rval));

            // Verify it got in there
            if (rval.getFitness() == null) {
                throw new IllegalStateException("ERROR: Fitness Function didn't correctly set fitness value for " + rval);
            }

            // Now add to the target Population

            targetPopulation.addIndividual(rval);

            // Now re-add this guy to the processing queue
            AVAILABLE_WP.add(this);

        } catch (Exception e) {
            lamark.exceptionInSubunit(e);
        }
    }

    /**
     * Private enumeration of work package types
     *
     * @author cweiss
     */
    enum Type {
        /**
         * Create a new individual via random creation *
         */
        NEW,
        /**
         * Create a new individual by copying an old one *
         */
        COPY,
        /**
         * Create a new individual via crossover *
         */
        CROSSOVER
    }
}
