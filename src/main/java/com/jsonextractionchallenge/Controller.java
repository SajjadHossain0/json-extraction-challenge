package com.jsonextractionchallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsonextractionchallenge.DTO.ExtractedData;
import com.jsonextractionchallenge.DTO.ExtractionResponse;
import com.jsonextractionchallenge.DTO.ImageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "https://json-extraction-challenge.intellixio.com")
public class Controller {

    private final OcrService ocrService;
    private final ObjectMapper objectMapper;

    // Inject both dependencies via constructor
    public Controller(OcrService ocrService, ObjectMapper objectMapper) {
        this.ocrService = ocrService;
        this.objectMapper = objectMapper;
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
}



