package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that is passed an individual and returns a mutated version of it
 *
 * Note - this method should ALWAYS return a new object, never modify the original
 * Method should take the form T fnName(T input, {optional params})
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { METHOD })
@Retention(RUNTIME)
@Documented
public @interface Mutator {
    String description() default "Mutator";
}
