package com.nirman.ledger.service;

import com.nirman.ledger.dto.ExpenseRequest;
import com.nirman.ledger.dto.ExpenseResponse;
import com.nirman.ledger.model.Expense;
import com.nirman.ledger.model.Site;
import com.nirman.ledger.model.User;
import com.nirman.ledger.repository.ExpenseRepository;
import com.nirman.ledger.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final SiteRepository siteRepository;
    private final SiteService siteService;
    private final FirebaseStorageService storageService;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request, MultipartFile file, User contractor) {
        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to record expenses for this site");
        }

        String receiptUrl = null;
        if (file != null && !file.isEmpty()) {
            receiptUrl = storageService.uploadMultipartFile(file);
        }

        Expense expense = Expense.builder()
                .site(site)
                .category(request.getCategory())
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .receiptUrl(receiptUrl)
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    public List<ExpenseResponse> getExpensesBySite(Long siteId, User user) {
        siteService.getSiteById(siteId, user);
        return expenseRepository.findBySiteId(siteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .siteId(expense.getSite().getId())
                .siteName(expense.getSite().getSiteName())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .description(expense.getDescription())
                .receiptUrl(expense.getReceiptUrl())
                .build();
    }
}
