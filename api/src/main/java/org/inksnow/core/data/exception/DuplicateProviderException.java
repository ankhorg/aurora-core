package org.inksnow.core.data.exception;

public final class DuplicateProviderException extends RuntimeException {
    public DuplicateProviderException() {
    }

    public DuplicateProviderException(String message) {
        super(message);
    }

    public DuplicateProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateProviderException(Throwable cause) {
        super(cause);
    }
}
