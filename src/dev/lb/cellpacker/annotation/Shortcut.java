package dev.lb.cellpacker.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotated methods are shortcuts for other methods. The name of a method call
 * with the same result is stored in the value parameter of this annotation.
 * @author Lars b.
 * @version 1.0
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Shortcut {
	String value();
}
