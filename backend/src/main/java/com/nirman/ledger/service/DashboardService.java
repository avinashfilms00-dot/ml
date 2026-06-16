package com.nirman.ledger.service;

import com.nirman.ledger.dto.DashboardResponse;
import com.nirman.ledger.model.*;
import com.nirman.ledger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SiteService siteService;
    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExpenseRepository expenseRepository;
    private final PayrollRepository payrollRepository;

    public DashboardResponse getDashboardStats(Long siteId, User user) {
        // Site service verifies access
        siteService.getSiteById(siteId, user);

        // 1. Total workers
        long totalWorkers = workerRepository.findBySiteIdAndActive(siteId, true).size();

        // 2. Present today
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.findBySiteIdAndDate(siteId, today).stream()
                .filter(att -> att.getStatus() == AttendanceStatus.PRESENT || att.getStatus() == AttendanceStatus.HALF_DAY)
                .count();

        // 3. Weekly payroll cost
        int dayOfWeekVal = today.getDayOfWeek().getValue();
        LocalDate sunday = (dayOfWeekVal == 7) ? today : today.minusDays(dayOfWeekVal);
        LocalDate saturday = sunday.plusDays(6);

        List<Payroll> weeklyPayrolls = payrollRepository.findBySiteIdAndStartDateAndEndDate(siteId, sunday, saturday);
        BigDecimal weeklyPayrollCost = weeklyPayrolls.stream()
                .map(Payroll::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Expenses
        List<Expense> expenses = expenseRepository.findBySiteId(siteId);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Pending payroll amount
        List<Payroll> allPayrolls = payrollRepository.findBySiteId(siteId);
        BigDecimal pendingAmount = allPayrolls.stream()
                .filter(p -> p.getStatus() == PayrollStatus.PENDING)
                .map(Payroll::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .totalWorkers(totalWorkers)
                .presentToday(presentToday)
                .weeklyPayrollCost(weeklyPayrollCost)
                .totalExpenses(totalExpenses)
                .pendingAmount(pendingAmount)
                .build();
    }
}
