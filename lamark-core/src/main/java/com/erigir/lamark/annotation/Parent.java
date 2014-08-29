package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A method marked as a crossover should take one or more "parent" parameters (of the same time)
 * <p/>
 * Created by chrweiss on 7/22/14.
 */
@Target({PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface Parent {
    String value() default "Parent Parameter";
}
