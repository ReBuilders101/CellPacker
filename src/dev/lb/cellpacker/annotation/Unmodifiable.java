package dev.lb.cellpacker.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotated methods return unmodifiable/immutable objects.
 * @author Lars B.
 * @version 1.0
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Unmodifiable {
}
