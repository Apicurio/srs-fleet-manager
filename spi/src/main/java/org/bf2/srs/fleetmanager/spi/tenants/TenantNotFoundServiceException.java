package org.bf2.srs.fleetmanager.spi.tenants;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class TenantNotFoundServiceException extends Exception {

    public TenantNotFoundServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
