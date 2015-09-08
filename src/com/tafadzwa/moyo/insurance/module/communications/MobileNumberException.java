package com.tafadzwa.moyo.insurance.module.communications;

public class MobileNumberException extends Exception {
    public MobileNumberException(String message, Throwable cause) {
        super(message, cause);
    }
    public MobileNumberException(String message) {
        super(message);
    }
}
