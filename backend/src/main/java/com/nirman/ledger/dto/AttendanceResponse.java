package com.nirman.ledger.dto;

import com.nirman.ledger.model.AttendanceStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceResponse {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private LocalDate date;
    private AttendanceStatus status;
}
