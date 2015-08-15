package com.erigir.lamark.config;

import com.erigir.lamark.*;

import java.util.List;

/**
 * This object extends a standard LamarkConfig to add drivers for the GUI panel (allowing selection from a list)
 * <p/>
 * This class is itself a valid implementation of LamarkConfig, but also allows a list of the various
 * pieces to be selected from (for driving the GUI)
 * User: cweiss
 * Date: 2/15/14
 * Time: 1:28 PM
 */
public class LamarkGUIConfig extends LamarkConfig {

    /**
     * Handle to the creator components available *
     */
    private List<Class<? extends ICreator>> creatorClasses;
    /**
     * Handle to the crossover components available *
     */
    private List<Class<? extends ICrossover>> crossoverClasses;
    /**
     * Handle to the fitness function components available *
     */
    private List<Class<? extends IFitnessFunction>> fitnessFunctionClasses;
    /**
     * Handle to the mutator components available *
     */
    private List<Class<? extends IMutator>> mutatorClasses;
    /**
     * Handle to the selector components available *
     */
    private List<Class<? extends ISelector>> selectorClasses;
    /**
     * Handle to the individual formatters available *
     */
    private List<Class<? extends IIndividualFormatter>> individualFormatterClasses;


    public Class<? extends ICreator> defaultCreator() {
        return (getCreatorClass() == null) ? creatorClasses.get(0) : getCreatorClass();
    }

    public Class<? extends ICrossover> defaultCrossover() {
        return (getCrossoverClass() == null) ? crossoverClasses.get(0) : getCrossoverClass();
    }

    public Class<? extends IFitnessFunction> defaultFitnessFunction() {
        return (getFitnessFunctionClass() == null) ? fitnessFunctionClasses.get(0) : getFitnessFunctionClass();
    }

    public Class<? extends ISelector> defaultSelector() {
        return (getSelectorClass() == null) ? selectorClasses.get(0) : getSelectorClass();
    }

    public Class<? extends IMutator> defaultMutator() {
        return (getMutatorClass() == null) ? mutatorClasses.get(0) : getMutatorClass();
    }

    public List<Class<? extends ICreator>> getCreatorClasses() {
        return creatorClasses;
    }

    public void setCreatorClasses(List<Class<? extends ICreator>> creatorClasses) {
        this.creatorClasses = creatorClasses;
    }

    public List<Class<? extends ICrossover>> getCrossoverClasses() {
        return crossoverClasses;
    }

    public void setCrossoverClasses(List<Class<? extends ICrossover>> crossoverClasses) {
        this.crossoverClasses = crossoverClasses;
    }

    public List<Class<? extends IFitnessFunction>> getFitnessFunctionClasses() {
        return fitnessFunctionClasses;
    }

    public void setFitnessFunctionClasses(List<Class<? extends IFitnessFunction>> fitnessFunctionClasses) {
        this.fitnessFunctionClasses = fitnessFunctionClasses;
    }

    public List<Class<? extends IMutator>> getMutatorClasses() {
        return mutatorClasses;
    }

    public void setMutatorClasses(List<Class<? extends IMutator>> mutatorClasses) {
        this.mutatorClasses = mutatorClasses;
    }

    public List<Class<? extends ISelector>> getSelectorClasses() {
        return selectorClasses;
    }

    public void setSelectorClasses(List<Class<? extends ISelector>> selectorClasses) {
        this.selectorClasses = selectorClasses;
    }

    public List<Class<? extends IIndividualFormatter>> getIndividualFormatterClasses() {
        return individualFormatterClasses;
    }

    public void setIndividualFormatterClasses(List<Class<? extends IIndividualFormatter>> individualFormatterClasses) {
        this.individualFormatterClasses = individualFormatterClasses;
    }
}


