package com.example.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    
    public ErrorResponse(boolean success, String message, int status, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
