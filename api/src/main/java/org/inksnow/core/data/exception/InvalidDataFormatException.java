package org.inksnow.core.data.exception;

public final class InvalidDataFormatException extends UnsupportedOperationException {
    public InvalidDataFormatException() {
    }

    public InvalidDataFormatException(String message) {
        super(message);
    }

    public InvalidDataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataFormatException(Throwable cause) {
        super(cause);
    }
}
