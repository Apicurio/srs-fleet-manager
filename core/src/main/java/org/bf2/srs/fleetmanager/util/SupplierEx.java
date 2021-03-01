package org.bf2.srs.fleetmanager.util;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@FunctionalInterface
public interface SupplierEx<T> {

    T get() throws Exception;
}
