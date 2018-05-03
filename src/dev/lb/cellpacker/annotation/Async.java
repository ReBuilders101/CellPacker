package dev.lb.cellpacker.annotation;

import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotated methods should be called asynchonously, because they may be blocking the thread for some time.
 * @author Lars
 *
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Async {

}
