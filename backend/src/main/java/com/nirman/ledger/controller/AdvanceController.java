package com.nirman.ledger.controller;

import com.nirman.ledger.dto.AdvanceRequest;
import com.nirman.ledger.dto.AdvanceResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.AdvanceService;
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
@RequestMapping("/advance")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Advance", description = "Endpoints for logging worker advances")
public class AdvanceController {

    private final AdvanceService advanceService;

    @PostMapping
    @Operation(summary = "Give and record a wage advance to a worker (Contractor only)")
    public ResponseEntity<AdvanceResponse> createAdvance(
            @Valid @RequestBody AdvanceRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(advanceService.createAdvance(request, user));
    }

    @GetMapping
    @Operation(summary = "Get all advance records for a site")
    public ResponseEntity<List<AdvanceResponse>> getAdvancesBySite(
            @RequestParam Long siteId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(advanceService.getAdvancesBySite(siteId, user));
    }
}
