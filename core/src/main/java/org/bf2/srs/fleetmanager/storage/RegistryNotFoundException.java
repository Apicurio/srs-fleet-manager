package org.bf2.srs.fleetmanager.storage;

public class RegistryNotFoundException extends StorageException {

    private static final long serialVersionUID = 3830931257679125603L;

    public RegistryNotFoundException() {
        super();
    }

    public RegistryNotFoundException(String message) {
        super(message);
    }

    public static RegistryNotFoundException create(Long id) {
        return new RegistryNotFoundException("No Registry found for id " + id);
    }
}
