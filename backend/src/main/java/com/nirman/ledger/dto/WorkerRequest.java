package com.nirman.ledger.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WorkerRequest {

    @NotBlank(message = "Worker name is required")
    private String name;

    @NotBlank(message = "Worker mobile is required")
    private String mobile;

    @NotBlank(message = "Skill is required")
    private String skill;

    @NotNull(message = "Daily wage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily wage must be greater than zero")
    private BigDecimal dailyWage;

    private Boolean active; // Default true if null

    @NotNull(message = "Site ID is required")
    private Long siteId;
}
