package org.bf2.srs.fleetmanager.common.storage;

import lombok.Getter;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

public class RegistryNotFoundException extends StorageException implements UserError {

    private static final long serialVersionUID = 3830931257679125603L;

    @Getter
    private final String registryId;

    public RegistryNotFoundException(long registryId) {
        this.registryId = Long.toString(registryId);
    }

    public RegistryNotFoundException(String registryId) {
        this.registryId = registryId;
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_REGISTRY_NOT_FOUND, registryId);
    }
}
