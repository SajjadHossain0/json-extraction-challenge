package com.jsonextractionchallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/")
public class Controller {

    private final OcrService ocrService;
    private final ObjectMapper objectMapper;

    // Inject both dependencies via constructor
    public Controller(OcrService ocrService, ObjectMapper objectMapper) {
        this.ocrService = ocrService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("test")
    public String test() {
        return "API is working";
    }

    @PostMapping
    public ResponseEntity<ExtractionResponse> extractJsonFromImage(@RequestBody ImageRequest request) {
        try {
            // Validate input
            if (request.getImageBase64() == null || request.getImageBase64().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ExtractionResponse(false, null, "Empty image data")
                );
            }

            // Perform OCR
            String extractedText = ocrService.performOcr(request.getImageBase64());

            // Clean and format the extracted text
            String jsonText = formatExtractedText(extractedText);

            // Try to parse as JSON
            try {
                ExtractedData extractedData = objectMapper.readValue(jsonText, ExtractedData.class);
                return ResponseEntity.ok(
                        new ExtractionResponse(true, extractedData, "Successfully extracted JSON from image")
                );
            } catch (JsonProcessingException e) {
                return ResponseEntity.badRequest().body(
                        new ExtractionResponse(false, null,
                                "Extracted text is not valid JSON. Error: " + e.getMessage() +
                                        "\nExtracted text: " + jsonText)
                );
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ExtractionResponse(false, null,
                            "Processing error: " + e.getMessage())
            );
        }
    }

    private String formatExtractedText(String extractedText) {
        // Remove any asterisks or other OCR artifacts
        String cleanedText = extractedText.replace("*", "");

        // Trim whitespace and newlines
        cleanedText = cleanedText.trim();

        // Ensure the text is wrapped in curly braces if not already
        if (!cleanedText.startsWith("{")) {
            cleanedText = "{" + cleanedText;
        }
        if (!cleanedText.endsWith("}")) {
            cleanedText = cleanedText + "}";
        }

        // Fix any common OCR errors
        cleanedText = cleanedText.replace("\n", "").replace("\r", "");

        return cleanedText;
    }



    // Request and Response classes
    public static class ImageRequest {
        private String imageBase64;

        public String getImageBase64() {
            return imageBase64;
        }

        public void setImageBase64(String imageBase64) {
            this.imageBase64 = imageBase64;
        }
    }

    public static class ExtractionResponse {
        private boolean success;
        private ExtractedData data;
        private String message;

        public ExtractionResponse(boolean success, ExtractedData data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public ExtractedData getData() { return data; }
        public String getMessage() { return message; }
    }

    public static class ExtractedData {
        private String name;
        private String organization;
        private String address;
        private String mobile;

        // Default constructor for JSON deserialization
        public ExtractedData() {}

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
    }
}



// K86152584688957

