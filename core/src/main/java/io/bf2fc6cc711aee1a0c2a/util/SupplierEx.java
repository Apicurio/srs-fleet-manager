package io.bf2fc6cc711aee1a0c2a.util;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@FunctionalInterface
public interface SupplierEx<T> {

    T get() throws Exception;
}
