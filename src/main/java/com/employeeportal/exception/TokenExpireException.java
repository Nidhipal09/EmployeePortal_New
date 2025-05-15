package com.employeeportal.exception;

public class TokenExpireException extends RuntimeException {
    public TokenExpireException() {
        super("your token has been expire,please try again with new token");
    }
}
