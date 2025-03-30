package com.jsonextractionchallenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    private final OcrService ocrService;

    public Controller(OcrService ocrService) {
        this.ocrService = ocrService;
    }


    @PostMapping()
    public ResponseEntity<ExtractionResponse> extractJsonFromImage(@RequestBody ImageRequest request) {
        try {
            // Extract the actual base64 content (remove data:image/png;base64, prefix if present)
            String base64Image = request.getImageBase64().split(",")[1];

            // Perform OCR
            String extractedText = ocrService.performOcr(base64Image);

            // Parse the JSON
            Object jsonData = new ObjectMapper().readValue(extractedText, Object.class);

            return ResponseEntity.ok(
                    new ExtractionResponse(true, jsonData, "Successfully extracted JSON from image")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ExtractionResponse(false, null, "Error processing image: " + e.getMessage())
            );
        }
    }
}

// AIzaSyBSGN_Af_IJ4eL0k8t_dY8FWVwM734FldQ
