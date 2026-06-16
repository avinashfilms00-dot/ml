package com.nirman.ledger.controller;

import com.nirman.ledger.dto.PayrollResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payroll", description = "Endpoints for weekly payroll calculation and payout management")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/weekly")
    @Operation(summary = "Generate/View the weekly payroll records for a site and date")
    public ResponseEntity<List<PayrollResponse>> getWeeklyPayroll(
            @RequestParam Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(payrollService.getOrGenerateWeeklyPayroll(siteId, date, user));
    }

    @PostMapping("/pay")
    @Operation(summary = "Mark a payroll record as paid (Contractor only)")
    public ResponseEntity<PayrollResponse> payPayroll(
            @RequestParam Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(payrollService.payPayroll(id, user));
    }
}
