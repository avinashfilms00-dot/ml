package com.nirman.ledger.controller;

import com.nirman.ledger.dto.SiteRequest;
import com.nirman.ledger.dto.SiteResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Sites", description = "Endpoints for managing construction sites")
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    @Operation(summary = "Create a new construction site (Contractor only)")
    public ResponseEntity<SiteResponse> createSite(
            @Valid @RequestBody SiteRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(siteService.createSite(request, user));
    }

    @GetMapping
    @Operation(summary = "Get all sites associated with the authenticated user")
    public ResponseEntity<List<SiteResponse>> getAllSites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(siteService.getAllSites(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a site by ID")
    public ResponseEntity<SiteResponse> getSiteById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(siteService.getSiteById(id, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update site details (Contractor only)")
    public ResponseEntity<SiteResponse> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody SiteRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(siteService.updateSite(id, request, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete site (Contractor only)")
    public ResponseEntity<Void> deleteSite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        siteService.deleteSite(id, user);
        return ResponseEntity.noContent().build();
    }
}
