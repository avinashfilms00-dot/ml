package com.nirman.ledger.model;

import java.math.BigDecimal;

public class DashboardResponse {
    private long totalWorkers;
    private long presentToday;
    private BigDecimal weeklyPayrollCost;
    private BigDecimal totalExpenses;
    private BigDecimal pendingAmount;

    public DashboardResponse() {}

    public long getTotalWorkers() { return totalWorkers; }
    public void setTotalWorkers(long totalWorkers) { this.totalWorkers = totalWorkers; }

    public long getPresentToday() { return presentToday; }
    public void setPresentToday(long presentToday) { this.presentToday = presentToday; }

    public BigDecimal getWeeklyPayrollCost() { return weeklyPayrollCost; }
    public void setWeeklyPayrollCost(BigDecimal weeklyPayrollCost) { this.weeklyPayrollCost = weeklyPayrollCost; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public BigDecimal getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }
}
