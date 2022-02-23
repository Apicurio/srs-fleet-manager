package org.bf2.srs.fleetmanager.spi.ams.impl.exception;

import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.impl.model.response.Error;
import org.bf2.srs.fleetmanager.spi.ams.model.AMSError;

import java.util.Optional;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class ExceptionConvert {

    private ExceptionConvert() {
    }

    public static void convert(AccountManagementSystemClientException ex) throws AccountManagementServiceException {
        throw new AccountManagementServiceException(mapCauseEntity(ex.getCauseEntity()), ex.getStatusCode(), ex);
    }

    public static void convertWithSubscriptionNotFound(AccountManagementSystemClientException ex) throws SubscriptionNotFoundServiceException, AccountManagementServiceException {
        if (ex.getStatusCode().isPresent() && ex.getStatusCode().get() == 404) {
            throw new SubscriptionNotFoundServiceException(mapCauseEntity(ex.getCauseEntity()), ex);
        }
        convert(ex);
    }

    private static Optional<AMSError> mapCauseEntity(Optional<Error> causeEntity) {
        return causeEntity.map(e -> AMSError.builder()
                .code(e.getCode())
                .reason(e.getReason())
                .build());
    }
}
