package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that is given a list of individuals and selects n of them
 *
 * NOTE - Selectors must recieve a list of Individual(T) objects, not the objects
 * themselves (because most selectors use the fitness of the object in selection)
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { METHOD })
@Retention(RUNTIME)
@Documented
public @interface Selector {
    String description() default "Selector";
}
