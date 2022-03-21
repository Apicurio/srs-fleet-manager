package org.bf2.srs.fleetmanager.spi.ams;

import org.bf2.srs.fleetmanager.spi.ams.model.AMSError;

import java.util.Optional;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class SubscriptionNotFoundServiceException extends Exception {

    private static final long serialVersionUID = -5654811641954127347L;

    public SubscriptionNotFoundServiceException(Optional<AMSError> causeEntity, Exception cause) {
        super(causeEntity.map(AMSError::toString).orElse(cause.getMessage()), cause);
    }
}
