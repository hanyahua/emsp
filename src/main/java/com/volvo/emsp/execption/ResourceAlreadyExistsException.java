package com.volvo.emsp.execption;

@SuppressWarnings("unused")
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
