package com.nirman.ledger.dto;

import com.nirman.ledger.model.SiteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SiteRequest {

    @NotBlank(message = "Site name is required")
    private String siteName;

    @NotBlank(message = "Site address is required")
    private String address;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner mobile is required")
    private String ownerMobile;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private SiteStatus status; // Can be null, default to ACTIVE
}
