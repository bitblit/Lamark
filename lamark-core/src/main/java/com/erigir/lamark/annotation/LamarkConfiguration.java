package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A lamark configuration object marks a class that holds everything necessary to run a lamark instance
 * <p/>
 * Created by chrweiss on 7/22/14.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface LamarkConfiguration {

}
