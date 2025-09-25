package com.main.projetstage.services;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

public interface ReportService {

    void generateDetailedRevenuePdf(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException;

    // You can add more report generation methods here if needed
}