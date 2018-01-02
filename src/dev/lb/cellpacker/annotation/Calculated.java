package dev.lb.cellpacker.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The object returned by the annotated method is newly cfeated eery time the method is called
 * This method should only be used in cases where it isn't obvoius that the returned object is not
 * a field or stored in a variable.
 * @author Lars b.
 * @version 1.0
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Calculated {

}
