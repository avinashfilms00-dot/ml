package com.nirman.ledger.controller;

import com.nirman.ledger.dto.ExpenseRequest;
import com.nirman.ledger.dto.ExpenseResponse;
import com.nirman.ledger.model.ExpenseCategory;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Expenses", description = "Endpoints for managing site expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Log a new site expense with optional receipt file upload (Contractor only)")
    public ResponseEntity<ExpenseResponse> createExpense(
            @RequestParam("siteId") Long siteId,
            @RequestParam("category") ExpenseCategory category,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "receipt", required = false) MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        ExpenseRequest request = new ExpenseRequest();
        request.setSiteId(siteId);
        request.setCategory(category);
        request.setAmount(amount);
        request.setDate(date);
        request.setDescription(description);
        return ResponseEntity.ok(expenseService.createExpense(request, file, user));
    }

    @GetMapping
    @Operation(summary = "Retrieve all expense records logged at a site")
    public ResponseEntity<List<ExpenseResponse>> getExpensesBySite(
            @RequestParam Long siteId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(expenseService.getExpensesBySite(siteId, user));
    }
}
