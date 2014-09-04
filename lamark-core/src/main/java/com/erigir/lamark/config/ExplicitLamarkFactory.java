package com.erigir.lamark.config;

import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.annotation.*;
import com.erigir.lamark.selector.ISelector;
import com.erigir.lamark.selector.RouletteWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Creates a Lamark instance from an explicit set of DynamicMethodWrappers
 * Created by chrweiss on 9/1/14.
 */
public class ExplicitLamarkFactory implements ILamarkFactory{
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitLamarkFactory.class);

    /**
     * Holder for all runtime parameters
     */
    private Map<String, Object> runtimeParameters = new TreeMap<>();
    /**
     * Handle to the creator component *
     */
    private DynamicMethodWrapper<Creator> creator;
    /**
     * Handle to the crossover component *
     */
    private DynamicMethodWrapper<Crossover> crossover;
    /**
     * Handle to the fitness function component *
     */
    private DynamicMethodWrapper<FitnessFunction> fitnessFunction;
    /**
     * Handle to the mutator component *
     */
    private DynamicMethodWrapper<Mutator> mutator;
    /**
     * Handle to the selector component, defaulted to RouletteWheel *
     */
    private ISelector selector = new RouletteWheel();
    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    private DynamicMethodWrapper<IndividualFormatter> formatter;
    /**
     * Handle to the preloader if any *
     */
    private DynamicMethodWrapper<PreloadIndividuals> preloader;

    /**
     * Handle to the individual formatter, used for printing individuals into messages *
     */
    private Set<DynamicMethodWrapper<LamarkEventListener>> listeners = new HashSet<>();

    @Override
    public Lamark createConfiguredLamarkInstance() {
        Lamark rval = new Lamark();
        rval.setRuntimeParameters(runtimeParameters);
        rval.setCreator(creator);
        rval.setCrossover(crossover);
        rval.setFitnessFunction(fitnessFunction);
        rval.setMutator(mutator);
        rval.setSelector(selector);
        rval.setFormatter(formatter);
        rval.setPreloader(preloader);
        rval.setListeners(listeners);

        return rval;
    }

    @Override
    public String getShortDescription() {
        return "ExplicitLamarkFactory";
    }

    public Map<String, Object> getRuntimeParameters() {
        return runtimeParameters;
    }

    public void setRuntimeParameters(Map<String, Object> runtimeParameters) {
        this.runtimeParameters = runtimeParameters;
    }

    public DynamicMethodWrapper<Creator> getCreator() {
        return creator;
    }

    public void setCreator(DynamicMethodWrapper<Creator> creator) {
        this.creator = creator;
    }

    public DynamicMethodWrapper<Crossover> getCrossover() {
        return crossover;
    }

    public void setCrossover(DynamicMethodWrapper<Crossover> crossover) {
        this.crossover = crossover;
    }

    public DynamicMethodWrapper<FitnessFunction> getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(DynamicMethodWrapper<FitnessFunction> fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public DynamicMethodWrapper<Mutator> getMutator() {
        return mutator;
    }

    public void setMutator(DynamicMethodWrapper<Mutator> mutator) {
        this.mutator = mutator;
    }

    public ISelector getSelector() {
        return selector;
    }

    public void setSelector(ISelector selector) {
        this.selector = selector;
    }

    public DynamicMethodWrapper<IndividualFormatter> getFormatter() {
        return formatter;
    }

    public void setFormatter(DynamicMethodWrapper<IndividualFormatter> formatter) {
        this.formatter = formatter;
    }

    public DynamicMethodWrapper<PreloadIndividuals> getPreloader() {
        return preloader;
    }

    public void setPreloader(DynamicMethodWrapper<PreloadIndividuals> preloader) {
        this.preloader = preloader;
    }

    public Set<DynamicMethodWrapper<LamarkEventListener>> getListeners() {
        return listeners;
    }

    public void setListeners(Set<DynamicMethodWrapper<LamarkEventListener>> listeners) {
        this.listeners = listeners;
    }
}
