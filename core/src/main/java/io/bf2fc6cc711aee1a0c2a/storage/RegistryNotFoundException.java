package io.bf2fc6cc711aee1a0c2a.storage;

public class RegistryNotFoundException extends RuntimeException {

    /**
     *
     */
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
