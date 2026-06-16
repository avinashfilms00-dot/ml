package com.nirman.ledger.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AdvanceResponse {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private BigDecimal amount;
    private LocalDate date;
    private String note;
}
