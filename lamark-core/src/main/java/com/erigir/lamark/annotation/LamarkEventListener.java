package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that should get called with the selected event is fired
 *
 * NOTE - These methods must have 1 parameter, which is assignable from LamarkEvent
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { METHOD })
@Retention(RUNTIME)
@Documented
public @interface LamarkEventListener {
    String description() default "Listener";
}
