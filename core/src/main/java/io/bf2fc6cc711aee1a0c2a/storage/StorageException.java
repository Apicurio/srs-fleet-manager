package io.bf2fc6cc711aee1a0c2a.storage;

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
