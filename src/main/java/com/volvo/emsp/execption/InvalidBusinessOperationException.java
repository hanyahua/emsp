package com.volvo.emsp.execption;


@SuppressWarnings("unused")
public class InvalidBusinessOperationException extends RuntimeException {

    public InvalidBusinessOperationException(String message) {
        super(message);
    }

    public InvalidBusinessOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBusinessOperationException(Throwable cause) {
        super(cause);
    }
}
