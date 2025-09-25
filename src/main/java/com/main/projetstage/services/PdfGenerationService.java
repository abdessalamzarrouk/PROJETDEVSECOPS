// src/main/java/com/yourproject/services/PdfGenerationService.java
// (Adjust package name as per your project structure)

package com.main.projetstage.services; // Example package

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class PdfGenerationService {

    private final WebClient webClient;

    public PdfGenerationService(WebClient.Builder webClientBuilder) {
        // Configure WebClient to talk to your Node.js service (Part 1, Step 3, PORT)
        this.webClient = webClientBuilder.baseUrl("http://localhost:3000").build();
    }

    public byte[] generatePdfFromHtml(String htmlContent) {
        // Create a JSON object to send to the Node.js service
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("htmlContent", htmlContent); // Key must match req.body.htmlContent in Node.js

        try {
            // Send a POST request with the HTML content and get the PDF bytes back
            ResponseEntity<byte[]> response = webClient.post()
                    .uri("/generate-pdf") // This matches the Node.js route (app.post('/generate-pdf'))
                    .contentType(MediaType.APPLICATION_JSON) // Tell the server we're sending JSON
                    .body(BodyInserters.fromValue(requestBody)) // The HTML content
                    .retrieve() // Execute the request
                    .toEntity(byte[].class) // Expect a byte array (the PDF) in response
                    .block(); // BLOCKING CALL: waits for response. For reactive apps, return Mono<byte[]>

            if (response != null && response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                System.out.println("PDF generation successful from Node.js service.");
                return response.getBody(); // Return the PDF data
            } else {
                System.err.println("Failed to get PDF from Node.js service. Status: " + (response != null ? response.getStatusCode() : "null"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error communicating with PDF generation service: " + e.getMessage());
            return null;
        }
    }
}