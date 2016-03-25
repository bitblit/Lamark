package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Method parameters annotated with param have the appropriate Lamark parameter injected at runtime
 * See list of Lamark parameters here
 * * Random (value=random)
 * * Lamark (value=lamark)
 * -- refs to each of the other items?
 * * Fitness (value=fitnessFunction)
 * * Creator (value=creator)
 * * Object with name (value=name)
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface Param {
    String value();
    boolean required() default true;
}
