package com.erigir.lamark;

import java.util.Comparator;

/**
 * Created by cweiss1271 on 11/18/15.
 */
public class ScoredGenome<T> {
    private T genome;
    private double fitness;

    public ScoredGenome() {
    }

    public ScoredGenome(T genome, double fitness) {
        this.genome = genome;
        this.fitness = fitness;
    }

    public T getGenome() {
        return genome;
    }

    public void setGenome(T genome) {
        this.genome = genome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public ScoredGenome withGenome(T genome) {
        this.genome = genome;
        return this;
    }
    public ScoredGenome withFitness(double fitness) {
        this.fitness = fitness;
        return this;
    }

}
