package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A marker interface for a class - designates the class should be scanned for other annotations.
 *
 * These classes should have a null constructor
 *
 * Created by chrweiss on 7/22/14.
 */
@Target( { TYPE })
@Retention(RUNTIME)
@Documented
public @interface LamarkComponent {
}
