package dev.lb.cellpacker.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Annotated methods should be called asynchonously, because they may be blocking the thread for some time.
 * @author Lars
 *
 */
@Retention(RUNTIME)
public @interface Async {

}
