package com.erigir.lamark.music.traits;

public class TraitWrapper {
    IMusicTrait trait;
    Double weight;

    public String toString() {
        return "Trait:" + trait.getClass() + " fitness=" + trait.getFitness() + " weight=" + weight;
    }

    public TraitWrapper(IMusicTrait pTrait, Double pWeight) {
        this.trait = pTrait;
        this.weight = pWeight;
    }

    public double getFitness() {
        return trait.getFitness();
    }

    public double getWeightedFitness() {
        return getFitness() * weight;
    }

    public IMusicTrait getTrait() {
        return trait;
    }

    public Double getWeight() {
        return weight;
    }
}
