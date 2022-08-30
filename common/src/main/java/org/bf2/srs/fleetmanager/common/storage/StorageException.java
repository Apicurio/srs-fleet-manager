package org.bf2.srs.fleetmanager.common.storage;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public abstract class StorageException extends Exception {

    private static final long serialVersionUID = -2753546889292434630L;

    public StorageException() {
        super();
    }

    public StorageException(String message) {
        super(message);
    }
}
