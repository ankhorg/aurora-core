package org.inksnow.core.data.exception;

public final class UnregisteredKeyException extends RuntimeException {
    public UnregisteredKeyException() {
    }

    public UnregisteredKeyException(String message) {
        super(message);
    }

    public UnregisteredKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnregisteredKeyException(Throwable cause) {
        super(cause);
    }
}
