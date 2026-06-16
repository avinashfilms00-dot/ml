package com.nirman.ledger.dto;

import com.nirman.ledger.model.AttendanceStatus;
import lombok.Data;

@Data
public class AttendanceRecordRequest {
    private Long workerId;
    private AttendanceStatus status;
}
