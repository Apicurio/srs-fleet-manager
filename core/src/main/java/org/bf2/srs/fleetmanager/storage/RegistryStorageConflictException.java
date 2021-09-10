package org.bf2.srs.fleetmanager.storage;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

/**
 * Since we have unified create & update methods, this is not a `NotFound` or `AlreadyExists` type error,
 * since then the entity would be created or updated, respectively.
 * <p>
 * This exception is usually a result of a ConstraintViolationException, because of e.g. uniqueness constraints.
 * <p>
 * Since validation is handled by the container using Hibernate Validator, this exception should not be used for that purpose.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class RegistryStorageConflictException extends StorageException implements UserError {

    private static final long serialVersionUID = -6235365490109511256L;

    public RegistryStorageConflictException() {
        super();
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_REGISTRY_DATA_CONFLICT);
    }
}
