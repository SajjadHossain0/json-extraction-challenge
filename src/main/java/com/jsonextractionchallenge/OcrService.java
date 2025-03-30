package com.jsonextractionchallenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OcrService {

    private final RestTemplate restTemplate;
    private final String tesseractApiUrl = "https://api.ocr.space/parse/image";

    public OcrService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String performOcr(String base64Image) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("apikey", "K86152584688957");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("base64Image", base64Image);
        body.add("language", "eng");
        body.add("isOverlayRequired", "false");
        body.add("filetype", "PNG");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                tesseractApiUrl,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            // Parse the OCR response to extract the text
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            return root.path("ParsedResults").get(0).path("ParsedText").asText();
        } else {
            throw new RuntimeException("OCR API request failed: " + response.getStatusCode());
        }
    }
}