package org.bf2.srs.fleetmanager.storage;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

public class RegistryDeploymentNotFoundException extends StorageException implements UserError {

    private static final long serialVersionUID = 7762437279168099113L;

    private final String registryDeploymentId;

    public RegistryDeploymentNotFoundException(String registryDeploymentId) {
        super();
        this.registryDeploymentId = registryDeploymentId;
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_REGISTRY_DEPLOYMENT_NOT_FOUND, registryDeploymentId);
    }
}
