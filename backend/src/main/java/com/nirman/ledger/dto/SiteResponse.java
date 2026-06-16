package com.nirman.ledger.dto;

import com.nirman.ledger.model.SiteStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class SiteResponse {
    private Long id;
    private String siteName;
    private String address;
    private String ownerName;
    private String ownerMobile;
    private LocalDate startDate;
    private SiteStatus status;
    private Long contractorId;
    private Long ownerId;
}
