package com.jsonextractionchallenge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExtractionResponse> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(
                new ExtractionResponse(false, null, "Error processing request: " + ex.getMessage())
        );
    }
}