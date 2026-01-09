package com.hungnguyen.coffee.restapitjava.exception;

// để bắt những exception ko thuộc bất cứ exception nào
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
