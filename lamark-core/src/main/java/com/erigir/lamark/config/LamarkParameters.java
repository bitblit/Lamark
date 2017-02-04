package com.erigir.lamark.config;

import com.erigir.lamark.*;
import lombok.Data;

import java.util.*;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkParameters {
    private Long randomSeed = null;
    private Double crossoverProbability = 1.0;
    private Double mutationProbability = .01;
    private Integer populationSize = 50;
    private Integer numberOfParents = 2;
    private Long maxGenerations = null;
    private List<String> initialValues = Collections.emptyList();
    private Double upperElitismPercentage=.05;
    private Double lowerElitismPercentage=.05;
    private Double targetScore=null;
    private Boolean trackParentage=false;
    private Boolean abortOnUniformPopulation=true;
    private Boolean runParallel=false;
    private Boolean minimizeScore=false;

    public LamarkBuilder applyToBuilder(LamarkBuilder builder) {
        builder.withRandom((randomSeed == null) ? new Random() : new Random(randomSeed));
        builder.withCrossoverProbability(crossoverProbability);
        builder.withMutationProbability(mutationProbability);
        builder.withPopulationSize(populationSize);
        builder.withNumberOfParents(numberOfParents);
        builder.withMaxGenerations(maxGenerations);
        //private List<String> initialValues = Collections.emptyList();
        builder.withUpperElitism(upperElitismPercentage);
        builder.withLowerElitism(lowerElitismPercentage);
        builder.withTargetScore(targetScore);
        builder.withTrackParentage(trackParentage);
        builder.withAbortOnUniformPopulation(abortOnUniformPopulation);
        builder.withRunParallel(runParallel);
        builder.withMinimizeScore(minimizeScore);

        return builder;
    }

}
