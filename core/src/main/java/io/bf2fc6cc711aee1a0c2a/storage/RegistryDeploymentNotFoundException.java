package io.bf2fc6cc711aee1a0c2a.storage;

public class RegistryDeploymentNotFoundException extends StorageException {

    private static final long serialVersionUID = 7762437279168099113L;

    public RegistryDeploymentNotFoundException() {
        super();
    }

    public RegistryDeploymentNotFoundException(String message) {
        super(message);
    }

    public static RegistryDeploymentNotFoundException create(Long id) {
        return new RegistryDeploymentNotFoundException("No RegistryDeployment found for id " + id);
    }

}
