package com.nirman.ledger.controller;

import com.nirman.ledger.dto.AttendanceRequest;
import com.nirman.ledger.dto.AttendanceResponse;
import com.nirman.ledger.model.User;
import com.nirman.ledger.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Attendance", description = "Endpoints for daily marking and calendar history")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Mark daily attendance for workers at a site (Contractor only)")
    public ResponseEntity<List<AttendanceResponse>> markAttendance(
            @Valid @RequestBody AttendanceRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(attendanceService.markAttendance(request, user));
    }

    @GetMapping
    @Operation(summary = "Retrieve site attendance history (Daily or Range)")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @RequestParam Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(attendanceService.getAttendance(siteId, date, endDate, user));
    }
}
