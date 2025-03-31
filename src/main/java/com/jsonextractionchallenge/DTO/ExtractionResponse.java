package com.jsonextractionchallenge.DTO;

public class ExtractionResponse {
    private boolean success;
    private ExtractedData data;
    private String message;

    public ExtractionResponse(boolean success, ExtractedData data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public ExtractionResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ExtractedData getData() {
        return data;
    }

    public void setData(ExtractedData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

