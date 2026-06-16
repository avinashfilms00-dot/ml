package com.nirman.ledger.dto;

import com.nirman.ledger.model.PayrollStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PayrollResponse {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int fullDays;
    private int halfDays;
    private BigDecimal dailyWage;
    private BigDecimal advanceDeducted;
    private BigDecimal finalAmount;
    private PayrollStatus status;
    private LocalDate paidDate;
}
