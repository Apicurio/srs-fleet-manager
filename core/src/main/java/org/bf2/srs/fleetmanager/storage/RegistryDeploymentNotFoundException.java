package org.bf2.srs.fleetmanager.storage;

public class RegistryDeploymentNotFoundException extends StorageException {

    private static final long serialVersionUID = 7762437279168099113L;

    @SuppressWarnings("unused")
    private final String registryDeploymentId;

    public RegistryDeploymentNotFoundException(String registryDeploymentId) {
        super(String.format("Registry Deployment with id='%s' not found", registryDeploymentId));
        this.registryDeploymentId = registryDeploymentId;
    }
}
