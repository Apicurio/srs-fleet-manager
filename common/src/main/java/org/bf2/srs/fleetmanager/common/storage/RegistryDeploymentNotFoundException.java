package org.bf2.srs.fleetmanager.common.storage;

public class RegistryDeploymentNotFoundException extends StorageException {

    private static final long serialVersionUID = 7762437279168099113L;

    public RegistryDeploymentNotFoundException() {
        super("Registry Deployment not found");
    }
}
