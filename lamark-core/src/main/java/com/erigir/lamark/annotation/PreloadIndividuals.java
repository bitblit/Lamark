package com.erigir.lamark.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a method that returns a list of preloaded individuals (optional)
 * <p/>
 * Created by chrweiss on 7/22/14.
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface PreloadIndividuals {
    String description() default "Preload individuals";
}
