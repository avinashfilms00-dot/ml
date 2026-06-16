package com.nirman.ledger.model;

import java.math.BigDecimal;

public class PayrollResponse {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private String startDate;
    private String endDate;
    private int fullDays;
    private int halfDays;
    private BigDecimal dailyWage;
    private BigDecimal advanceDeducted;
    private BigDecimal finalAmount;
    private String status; // PENDING, PAID
    private String paidDate;

    public PayrollResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getFullDays() { return fullDays; }
    public void setFullDays(int fullDays) { this.fullDays = fullDays; }

    public int getHalfDays() { return halfDays; }
    public void setHalfDays(int halfDays) { this.halfDays = halfDays; }

    public BigDecimal getDailyWage() { return dailyWage; }
    public void setDailyWage(BigDecimal dailyWage) { this.dailyWage = dailyWage; }

    public BigDecimal getAdvanceDeducted() { return advanceDeducted; }
    public void setAdvanceDeducted(BigDecimal advanceDeducted) { this.advanceDeducted = advanceDeducted; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }
}
