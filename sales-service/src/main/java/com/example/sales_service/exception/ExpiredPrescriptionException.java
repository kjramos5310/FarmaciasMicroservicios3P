package com.example.sales_service.exception;

public class ExpiredPrescriptionException extends RuntimeException {
    public ExpiredPrescriptionException(String message) {
        super(message);
    }
}
