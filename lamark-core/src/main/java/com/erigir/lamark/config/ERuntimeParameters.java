package com.erigir.lamark.config;

import java.util.Map;

public enum ERuntimeParameters {
    MAXIMUM_POPULATIONS("maximumPopulations",null,"Maximum number of generations to run.  If null, won't terminate due to generation number",Integer.class,false),
    POPULATION_SIZE("populationSize",100,"Number of individuals to have in a given generation.",Integer.class,true),
    /** upperElitism - Percent of the population to reserve across populations (ie, if set to .1 in a 20-individual population,
     * then the best 2 individuals will be copied to the next populations each cycle **/
    UPPER_ELITISM("upperElitism",0.0,"Percentage of individuals (0.0 - 1.0) to retain each generation as upper elitism.",Double.class,true),
    /** lowerElitism - Percent of the population to discard each generation (ie, if set to .1 in a 20-individual population, then 2
     * individuals will be generated from scratch each population.  NOTE - this essentially functions as a massive mutation program.  Use
     * with care to avoid simply performing a random walk of the search space **/
    LOWER_ELITISM("lowerElitism",0.0,"Percentage of individuals (0.0 - 1.0) to replace each generation as lower elitism.",Double.class,true),
    CROSSOVER_PROBABILITY("crossoverProbability",1.0,"Likelihood (0.0 - 1.0) of an individual being generated via crossover.  If the check fails, the first parent is copied instead.",Double.class,true),
    MUTATION_PROBABILITY("mutationProbability",0.005,"Likelihood (0.0 - 1.0) of an individual being mutated.  This is on an individual by individual basis, not a population-wide basis."+
            "Individuals retained via upper elitism are immune to mutation.",Double.class,true),
    NUMBER_OF_WORKER_THREADS("numberOfWorkerThreads",1,"Number of threads to use to process work packages.",Integer.class,true),
    TARGET_SCORE("targetScore",null,"A score which, if reached, will cause the algorithm to terminate.  If null, the algorithm will never terminate due to score",Double.class,false),
    TRACK_PARENTAGE("trackParentage",false,"Determines whether to track the parents of each individual.  Use with care, as turning this on will consume large amounts of memory "+
            "in a short period of time - defaults to false",Boolean.class,true),
    ABORT_ON_UNIFORM_POPULATION("abortOnUniformPopulation",true,"Determines whether the algorithm should stop if it ever reaches the state of all the individuals being equals (using .equals function) ", Boolean.class,true),
    RANDOM_SEED("randomSeed",null,"Seed to use for the random number generator.  Defaults to system current time.  Set to a constant to generate reproducible runs.  NOTE: runs" +
            " are only reproducible if this is a constant AND the number of worker threads is 1, since otherwise the order of processing is dependant on" +
            " OS scheduling", Long.class,false)

    ;

    String propertyName;
    Object defaultValue; // Anything without a default value (null) is required
    String description;
    Class type;
    boolean required;

    ERuntimeParameters(String propertyName, Object defaultValue, String description,Class type,boolean required)
    {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.description = description;
        this.type = type;
        this.required = required;
    }

    public Object read(Map<String, Object> params)
    {
        Object value = params.get(propertyName);
        if (value==null)
        {
            value = defaultValue;
        }
        if (value==null && required) // if its still null!
        {
            throw new IllegalStateException("Couldn't find value for required property "+propertyName+" in "+params);
        }
        if (value!=null && !type.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Property "+propertyName+" should be of type "+type+" but is of type "+value.getClass());
        }
        return value;
    }

    public <T> T read(Map<String, Object> params, Class<T> expectedClazz)
    {
        if (!type.isAssignableFrom(expectedClazz))
        {
            throw new IllegalArgumentException("Expected class "+expectedClazz+" not assignable from defined class "+type);
        }
        return (T)read(params);
    }

    public static void validate(Map<String,Object> params)
    {
        for (ERuntimeParameters e:ERuntimeParameters.values())
        {
            if (e.required)
            {
                e.read(params);
            }
        }
    }


}


