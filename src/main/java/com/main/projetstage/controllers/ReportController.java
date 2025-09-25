package com.main.projetstage.controllers;

import com.main.projetstage.models.OperationRecette; // Import the OperationRecette model
import com.main.projetstage.models.Fonctionnaire;
import com.main.projetstage.models.Utilisateur;
import com.main.projetstage.services.PdfGenerationService;
import com.main.projetstage.services.OperationRecetteService; // Import the new service

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.core.io.ClassPathResource; // Import for ClassPathResource
import java.io.InputStream; // Import for InputStream
import java.util.Base64; // Import for Base64

@Controller
public class ReportController {

    private final PdfGenerationService pdfGenerationService;
    private final OperationRecetteService operationRecetteService; // Use OperationRecetteService
    private final TemplateEngine templateEngine;

    public ReportController(PdfGenerationService pdfGenerationService,
                            OperationRecetteService operationRecetteService, // Inject OperationRecetteService
                            TemplateEngine templateEngine) {
        this.pdfGenerationService = pdfGenerationService;
        this.operationRecetteService = operationRecetteService; // Assign it
        this.templateEngine = templateEngine;
    }

    @GetMapping("/reports") // A new endpoint to display the form
    public String showReportForm() {
        return "report-form"; // This should match the name of your HTML file
    }

    @GetMapping("/reports/operations-recette") // Changed endpoint path
    public ResponseEntity<byte[]> generateOperationsRecetteReport( // Changed method name
                                                                   @RequestParam(name = "startDate", required = false) String startDateStr,
                                                                   @RequestParam(name = "endDate", required = false) String endDateStr) {

        LocalDate startDate = null;
        LocalDate endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = LocalDate.parse(startDateStr, formatter);
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = LocalDate.parse(endDateStr, formatter);
        }

        // --- Fetch Data for the Report ---
        List<OperationRecette> operations = operationRecetteService.findOperationsByDateRange(startDate, endDate);
        BigDecimal totalRevenue = operations.stream()
                .map(OperationRecette::getMontantRecette)
                .map(BigDecimal::valueOf) // Ensure this is correct if getMontantRecette returns double/float
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- Get the currently authenticated user's full name ---
        String reportGeneratorName = "N/A"; // Default value
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof Utilisateur) {
                Utilisateur currentUser = (Utilisateur) principal;
                if (currentUser.getFonctionnaire() != null) {
                    Fonctionnaire fonctionnaire = currentUser.getFonctionnaire();
                    reportGeneratorName = fonctionnaire.getNomPersonne() + " " + fonctionnaire.getPrenomPersonne();
                } else {
                    reportGeneratorName = currentUser.getNomUtilisateur();
                }
            } else {
                reportGeneratorName = authentication.getName();
            }
        }

        // --- Prepare Thymeleaf Context ---
        Context context = new Context();

        // --- IMPORTANT: Logic to convert SVG to Data URI ---
        String svgDataUri = "";
        try {
            // Ensure this path matches the actual location of your SVG in src/main/resources
            ClassPathResource resource = new ClassPathResource("static/images/TGR.svg");
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] svgBytes = inputStream.readAllBytes();
                String base64Svg = Base64.getEncoder().encodeToString(svgBytes);
                svgDataUri = "data:image/svg+xml;base64," + base64Svg;
            }
        } catch (Exception e) { // Catching generic Exception for robustness in file operations
            System.err.println("Error loading SVG logo: " + e.getMessage());
            // Fallback: A simple gray rectangle SVG as a placeholder if logo fails to load
            String fallbackSvg = "<svg xmlns='http://www.w3.org/2000/svg' width='100' height='50'><rect width='100%' height='100%' fill='#f8f8f8'/><text x='50%' y='50%' font-family='Arial' font-size='10' fill='#ccc' text-anchor='middle' dy='.3em'>Logo Error</text></svg>";
            svgDataUri = "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(fallbackSvg.getBytes());
        }
        context.setVariable("logoDataUri", svgDataUri);
        // --- End of SVG to Data URI Logic ---


        context.setVariable("reportTitle", "Rapport des Opérations de Recette");
        context.setVariable("companyName", "Trésorerie Générale du Royaume");
        context.setVariable("startDate", startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Début");
        context.setVariable("endDate", endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Fin");
        context.setVariable("reportGeneratedDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        context.setVariable("operations", operations);
        context.setVariable("totalRevenue", totalRevenue);
        context.setVariable("reportGeneratorName", reportGeneratorName);

        // Render your Thymeleaf template to a complete HTML string
        String htmlContent = templateEngine.process("operations-recette-report", context);

        // --- Call the PdfGenerationService to convert HTML to PDF ---
        byte[] pdfBytes = pdfGenerationService.generatePdfFromHtml(htmlContent);

        if (pdfBytes != null) {
            // --- Return the PDF to the User's Browser ---
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "operations_recette_report_" + LocalDate.now().format(formatter) + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, null, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}