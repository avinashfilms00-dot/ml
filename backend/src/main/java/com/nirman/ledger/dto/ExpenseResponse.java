package com.nirman.ledger.dto;

import com.nirman.ledger.model.ExpenseCategory;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private Long siteId;
    private String siteName;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String receiptUrl;
}
