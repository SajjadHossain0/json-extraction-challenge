package com.jsonextractionchallenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OcrService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String tesseractApiUrl = "https://api.ocr.space/parse/image";
    private final String ocrApiKey = "K86152584688957"; // Consider moving to config

    public OcrService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String performOcr(String base64Image) throws Exception {

        // Validate input
        if (base64Image == null || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Empty image data");
        }

        // Add white background if the image has a transparent background
        String imageWithWhiteBg = ImageProcessor.addWhiteBackgroundToImage(base64Image);

        // Clean base64 string and perform OCR as usual
        String cleanBase64 = imageWithWhiteBg.replaceFirst("^data:image/[^;]+;base64,", "");

        // Clean base64 string
        //String cleanBase64 = base64Image.replaceFirst("^data:image/[^;]+;base64,", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("apikey", ocrApiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("base64Image", "data:image/png;base64," + cleanBase64);
        body.add("language", "eng");
        body.add("isOverlayRequired", "false");
        body.add("filetype", "PNG");
        body.add("OCREngine", "2");
        body.add("scale", "true");
        body.add("detectOrientation", "true");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                tesseractApiUrl,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("OCR API returned status: " + response.getStatusCode());
        }

        JsonNode root = objectMapper.readTree(response.getBody());

        // Check for API errors
        if (root.has("ErrorMessage")) {
            throw new RuntimeException("OCR API error: " + root.path("ErrorMessage").asText());
        }

        JsonNode results = root.path("ParsedResults");

        if(results.isEmpty()){
            throw new RuntimeException("No results in OCR response");
        }

        String extractedText = results.get(0).path("ParsedText").asText().trim();

        if (extractedText.isEmpty()) {
            throw new RuntimeException("OCR succeeded but no text was extracted");
        }

        // Clean and format the extracted text to ensure valid JSON
        return formatExtractedText(extractedText);
    }

    private String formatExtractedText(String extractedText) {
        // Clean up the OCR artifacts
        String cleanedText = extractedText
                .replace("*", "")          // Remove any asterisks
                .replace("\"\"", "\"")     // Fix double quotes
                .replace("“", "\"")        // Replace curly quotes
                .replace("”", "\"")
                .replace("\n", "")         // Remove newlines
                .replace("\r", "")         // Remove carriage returns
                .replace(": ", ":")        // Remove spaces after colons
                .replace(", ", ",");       // Remove spaces after commas

        // Ensure that all keys are wrapped in double quotes
        cleanedText = cleanedText.replaceAll("(\\w+)\\s*:", "\"$1\":");

        // Fix the malformed "organization" field by removing extra quotes
        cleanedText = cleanedText.replaceAll("\"([^\"]+)\",([^\"]+)\"", "\"$1, $2\"");

        // Fix the "address" field by ensuring the full address value is enclosed in quotes
        cleanedText = cleanedText.replaceAll("\"address\":([^\"]+),([^\"]+)\"", "\"address\":\"$1, $2\"");

        // Ensure the text is wrapped in curly braces if not already
        if (!cleanedText.startsWith("{")) {
            cleanedText = "{" + cleanedText;
        }
        if (!cleanedText.endsWith("}")) {
            cleanedText = cleanedText + "}";
        }

        return cleanedText;
    }

}