package com.nirman.ledger.controller;

import com.nirman.ledger.dto.WorkerRequest;
import com.nirman.ledger.dto.WorkerResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.WorkerService;
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
@RequestMapping("/workers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Workers", description = "Endpoints for managing labor/workers")
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping
    @Operation(summary = "Add a new worker profile (Contractor only)")
    public ResponseEntity<WorkerResponse> createWorker(
            @Valid @RequestBody WorkerRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(workerService.createWorker(request, user));
    }

    @GetMapping
    @Operation(summary = "Get all workers associated with a construction site")
    public ResponseEntity<List<WorkerResponse>> getWorkersBySite(
            @RequestParam Long siteId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(workerService.getWorkersBySite(siteId, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update worker details (Contractor only)")
    public ResponseEntity<WorkerResponse> updateWorker(
            @PathVariable Long id,
            @Valid @RequestBody WorkerRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(workerService.updateWorker(id, request, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete worker profile (Contractor only)")
    public ResponseEntity<Void> deleteWorker(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        workerService.deleteWorker(id, user);
        return ResponseEntity.noContent().build();
    }
}
