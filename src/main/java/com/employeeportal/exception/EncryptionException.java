package com.employeeportal.exception;

public class EncryptionException extends RuntimeException {
    public EncryptionException() {
        super();
    }

    public EncryptionException(String msg) {
        super(msg);
    }
}
