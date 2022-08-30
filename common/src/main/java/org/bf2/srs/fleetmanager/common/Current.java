package org.bf2.srs.fleetmanager.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a bean that should be currently used.
 * This is not available for all beans, but is needed
 * in case the application needs to make a choice
 * from multiple potential implementations.
 * Where applicable, always use this qualifier at a given injection point.
 *
 * @author Jakub Senko <m@jsenko.net>
 */
@Qualifier
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
public @interface Current {
}
