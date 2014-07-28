package com.erigir.lamark.annotation;

import com.erigir.lamark.EFitnessType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that is passed an individual and returns the fitness value for it
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { METHOD })
@Retention(RUNTIME)
@Documented
public @interface FitnessFunction {
    EFitnessType fitnessType() default EFitnessType.MAXIMUM_BEST;
    String description() default "Fitness Function";
}
