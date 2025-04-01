# JSON Extraction Challenge

## Overview
This project implements an API that extracts structured JSON data from a base64-encoded image using Optical Character Recognition (OCR). The extracted text is cleaned and formatted before being returned in a structured response.

## Explainer Video
👉 **Watch the explainer video here**
This short video walks you through how the challenge works and what is expected.

## Objective
The goal of this challenge is to build a publicly accessible API endpoint that:
- Accepts a base64-encoded PNG image via a POST request.
- Extracts JSON data embedded in the image using OCR.
- Returns the extracted data in a structured JSON format.

## Implementation Details
### Features:
- OCR-based text extraction from images.
- JSON parsing and formatting.
- Error handling for invalid input and OCR processing issues.
- API testing with Postman.
- A frontend UI to test the API with different cases.

### Tech Stack:
- **Java** (Spring Boot)
- **OCR API** (Tesseract API)
- **Postman** (for API testing)
- **Frontend UI** (for validation and testing)

## API Specification
### Endpoint:
```
POST https://json-extraction-challenge.onrender.com/
```

### Request Format:
```json
{   
  "imageBase64": "data:image/png;base64,..."
}
```

### Expected Response Format:
```json
{   
  "success": true,   
  "data": {     
    "name": "Jane Smith",     
    "organization": "Beta Inc",     
    "address": "456 Elm Ave",     
    "mobile": "+1 555 5678"   
  },   
  "message": "Successfully extracted JSON from image"
}
```

### Error Responses:
- **Invalid Image Data:**
  ```json
  {   
    "success": false,   
    "data": null,   
    "message": "Empty image data"   
  }
  ```
- **Invalid JSON Extraction:**
  ```json
  {   
    "success": false,   
    "data": null,   
    "message": "Extracted text is not valid JSON"   
  }
  ```
- **OCR Processing Error:**
  ```json
  {   
    "success": false,   
    "data": null,   
    "message": "Processing error: [error details]"   
  }
  ```

## How to Test Your API
A frontend UI is available at:
👉 [https://json-extraction-challenge.intellixio.com/](https://json-extraction-challenge.intellixio.com/)

### Steps to Test:
1. The frontend generates a random JSON object.
2. It converts the JSON into a base64 image.
3. The image is sent to your API.
4. The API response is displayed.

Ensure that your API correctly extracts and returns the JSON data before submission.

## Code Structure
### Controller:
Handles the API request, calls the OCR service, and formats the extracted text.

### OCR Service:
Sends the base64 image to the OCR API, extracts text, and cleans it.

### JSON Formatter:
Cleans and ensures the extracted text is in valid JSON format.


## Contact

For any questions, feel free to reach out!
📩 Email: sajjad.tech.eng@gmail.com
💼 Portfolio: https://sajjadhossain.onrender.com/

