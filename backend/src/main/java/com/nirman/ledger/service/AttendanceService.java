package com.nirman.ledger.service;

import com.nirman.ledger.dto.AttendanceRecordRequest;
import com.nirman.ledger.dto.AttendanceRequest;
import com.nirman.ledger.dto.AttendanceResponse;
import com.nirman.ledger.model.*;
import com.nirman.ledger.repository.AttendanceRepository;
import com.nirman.ledger.repository.SiteRepository;
import com.nirman.ledger.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SiteRepository siteRepository;
    private final WorkerRepository workerRepository;
    private final SiteService siteService;

    @Transactional
    public List<AttendanceResponse> markAttendance(AttendanceRequest request, User contractor) {
        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to mark attendance for this site");
        }

        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceRecordRequest record : request.getRecords()) {
            Worker worker = workerRepository.findById(record.getWorkerId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + record.getWorkerId()));

            if (!worker.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException("Worker " + worker.getName() + " does not belong to site: " + site.getSiteName());
            }

            Optional<Attendance> existingAttendance = attendanceRepository.findByWorkerIdAndDate(
                    worker.getId(),
                    request.getDate()
            );

            Attendance attendance;
            if (existingAttendance.isPresent()) {
                attendance = existingAttendance.get();
                attendance.setStatus(record.getStatus());
            } else {
                attendance = Attendance.builder()
                        .worker(worker)
                        .site(site)
                        .date(request.getDate())
                        .status(record.getStatus())
                        .build();
            }

            Attendance saved = attendanceRepository.save(attendance);
            responses.add(mapToResponse(saved));
        }

        return responses;
    }

    public List<AttendanceResponse> getAttendance(Long siteId, LocalDate startDate, LocalDate endDate, User user) {
        // Verify user can access site
        siteService.getSiteById(siteId, user);

        List<Attendance> attendances;
        if (endDate != null) {
            attendances = attendanceRepository.findBySiteIdAndDateBetween(siteId, startDate, endDate);
        } else {
            attendances = attendanceRepository.findBySiteIdAndDate(siteId, startDate);
        }

        return attendances.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .workerId(attendance.getWorker().getId())
                .workerName(attendance.getWorker().getName())
                .siteId(attendance.getSite().getId())
                .siteName(attendance.getSite().getSiteName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .build();
    }
}
