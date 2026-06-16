package com.nirman.ledger.service;

import com.nirman.ledger.dto.PayrollResponse;
import com.nirman.ledger.model.*;
import com.nirman.ledger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final AdvanceRepository advanceRepository;
    private final SiteRepository siteRepository;
    private final SiteService siteService;

    @Transactional
    public List<PayrollResponse> getOrGenerateWeeklyPayroll(Long siteId, LocalDate date, User user) {
        // Verify user can access site
        siteService.getSiteById(siteId, user);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        // Calculate Sunday to Saturday week bounds
        int dayOfWeekVal = date.getDayOfWeek().getValue();
        LocalDate sunday;
        if (dayOfWeekVal == 7) { // Sunday
            sunday = date;
        } else {
            sunday = date.minusDays(dayOfWeekVal); // Align to previous Sunday
        }
        LocalDate saturday = sunday.plusDays(6);

        List<Worker> workers = workerRepository.findBySiteIdAndActive(siteId, true);
        List<Payroll> payrollList = new ArrayList<>();

        for (Worker worker : workers) {
            // Check if payroll record already exists for this worker and week
            Optional<Payroll> existingOpt = payrollRepository.findByWorkerIdAndStartDateAndEndDate(
                    worker.getId(),
                    sunday,
                    saturday
            );

            if (existingOpt.isPresent() && existingOpt.get().getStatus() == PayrollStatus.PAID) {
                // If already paid, do not recalculate
                payrollList.add(existingOpt.get());
                continue;
            }

            // Aggregate Attendance
            List<Attendance> attendances = attendanceRepository.findByWorkerIdAndDateBetween(
                    worker.getId(),
                    sunday,
                    saturday
            );

            int fullDays = 0;
            int halfDays = 0;
            for (Attendance att : attendances) {
                if (att.getStatus() == AttendanceStatus.PRESENT) {
                    fullDays++;
                } else if (att.getStatus() == AttendanceStatus.HALF_DAY) {
                    halfDays++;
                }
            }

            // Aggregate Advances
            List<Advance> advances = advanceRepository.findByWorkerIdAndDateBetween(
                    worker.getId(),
                    sunday,
                    saturday
            );

            BigDecimal totalAdvances = advances.stream()
                    .map(Advance::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculation:
            // finalAmount = (fullDays * dailyWage) + (halfDays * dailyWage / 2) - advance
            BigDecimal dailyWage = worker.getDailyWage();
            BigDecimal fullDaysAmt = dailyWage.multiply(new BigDecimal(fullDays));
            BigDecimal halfDaysAmt = dailyWage.multiply(new BigDecimal(halfDays)).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);
            BigDecimal grossWages = fullDaysAmt.add(halfDaysAmt);
            BigDecimal finalAmount = grossWages.subtract(totalAdvances);

            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO; // Cap at zero to prevent negative wage payout records
            }

            Payroll payroll;
            if (existingOpt.isPresent()) {
                payroll = existingOpt.get();
                payroll.setFullDays(fullDays);
                payroll.setHalfDays(halfDays);
                payroll.setDailyWage(dailyWage);
                payroll.setAdvanceDeducted(totalAdvances);
                payroll.setFinalAmount(finalAmount);
            } else {
                payroll = Payroll.builder()
                        .worker(worker)
                        .site(site)
                        .startDate(sunday)
                        .endDate(saturday)
                        .fullDays(fullDays)
                        .halfDays(halfDays)
                        .dailyWage(dailyWage)
                        .advanceDeducted(totalAdvances)
                        .finalAmount(finalAmount)
                        .status(PayrollStatus.PENDING)
                        .build();
            }

            // Save only if user is CONTRACTOR (since OWNER only reads and shouldn't mutate DB)
            if (user.getRole() == Role.ROLE_CONTRACTOR) {
                payroll = payrollRepository.save(payroll);
            }
            payrollList.add(payroll);
        }

        // Return generated/saved records
        return payrollList.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public PayrollResponse payPayroll(Long id, User contractor) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll record not found"));

        if (!payroll.getSite().getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to process payments for this site");
        }

        if (payroll.getStatus() == PayrollStatus.PAID) {
            throw new IllegalStateException("Payroll is already paid");
        }

        payroll.setStatus(PayrollStatus.PAID);
        payroll.setPaidDate(LocalDate.now());

        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    public PayrollResponse mapToResponse(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .workerId(payroll.getWorker().getId())
                .workerName(payroll.getWorker().getName())
                .siteId(payroll.getSite().getId())
                .siteName(payroll.getSite().getSiteName())
                .startDate(payroll.getStartDate())
                .endDate(payroll.getEndDate())
                .fullDays(payroll.getFullDays())
                .halfDays(payroll.getHalfDays())
                .dailyWage(payroll.getDailyWage())
                .advanceDeducted(payroll.getAdvanceDeducted())
                .finalAmount(payroll.getFinalAmount())
                .status(payroll.getStatus())
                .paidDate(payroll.getPaidDate())
                .build();
    }
}
