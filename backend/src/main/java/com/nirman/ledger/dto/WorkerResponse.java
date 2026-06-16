package com.nirman.ledger.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class WorkerResponse {
    private Long id;
    private String name;
    private String mobile;
    private String skill;
    private BigDecimal dailyWage;
    private boolean active;
    private Long siteId;
    private String siteName;
}
