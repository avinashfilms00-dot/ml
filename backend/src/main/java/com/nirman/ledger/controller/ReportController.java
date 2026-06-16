package com.nirman.ledger.controller;

import com.nirman.ledger.model.User;
import com.nirman.ledger.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Endpoints for generating site financial and attendance PDF reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    @Operation(summary = "Generate a Daily Report PDF and get its URL")
    public ResponseEntity<Map<String, String>> getDailyReport(
            @RequestParam Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        String pdfUrl = reportService.generateDailyReport(siteId, date, user);
        return ResponseEntity.ok(Map.of("pdfUrl", pdfUrl));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Generate a Weekly Report PDF (Sunday-Saturday week containing date) and get its URL")
    public ResponseEntity<Map<String, String>> getWeeklyReport(
            @RequestParam Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        String pdfUrl = reportService.generateWeeklyReport(siteId, date, user);
        return ResponseEntity.ok(Map.of("pdfUrl", pdfUrl));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Generate a Monthly Report PDF and get its URL")
    public ResponseEntity<Map<String, String>> getMonthlyReport(
            @RequestParam Long siteId,
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User user
    ) {
        String pdfUrl = reportService.generateMonthlyReport(siteId, year, month, user);
        return ResponseEntity.ok(Map.of("pdfUrl", pdfUrl));
    }
}
