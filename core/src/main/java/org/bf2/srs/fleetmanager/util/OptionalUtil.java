package org.bf2.srs.fleetmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class OptionalUtil {

    private static final Logger log = LoggerFactory.getLogger(OptionalUtil.class);

    public static <T> Optional<T> exceptionally(SupplierEx<T> code) {
        try {
            return ofNullable(code.get());
        } catch (Exception e) {
            log.debug("Caught an exception.", e);
            return empty();
        }
    }

    private OptionalUtil() {
    }
}
