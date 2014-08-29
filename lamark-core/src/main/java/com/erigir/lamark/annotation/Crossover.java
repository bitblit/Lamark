package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that, when called, is passed a list of individuals and creates a single new individual.
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { METHOD })
@Retention(RUNTIME)
@Documented
public @interface Crossover {
    String description() default "Crossover";
}
