package com.nirman.ledger.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private long totalWorkers;
    private long presentToday;
    private BigDecimal weeklyPayrollCost;
    private BigDecimal totalExpenses;
    private BigDecimal pendingAmount;
}
