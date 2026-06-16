package com.nirman.ledger.controller;

import com.nirman.ledger.dto.DashboardResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Endpoints for retrieving site metrics and financial KPIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get current KPI stats for the dashboard")
    public ResponseEntity<DashboardResponse> getDashboardStats(
            @RequestParam Long siteId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(dashboardService.getDashboardStats(siteId, user));
    }
}
