package com.example.sales_service.exception;

public class PrescriptionRequiredException extends RuntimeException {
    public PrescriptionRequiredException(String message) {
        super(message);
    }
}
