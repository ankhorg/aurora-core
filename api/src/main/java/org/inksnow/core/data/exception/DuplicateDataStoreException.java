package org.inksnow.core.data.exception;

public final class DuplicateDataStoreException extends RuntimeException {
    public DuplicateDataStoreException() {
    }

    public DuplicateDataStoreException(String message) {
        super(message);
    }

    public DuplicateDataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateDataStoreException(Throwable cause) {
        super(cause);
    }
}
