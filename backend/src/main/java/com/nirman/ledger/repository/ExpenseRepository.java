package com.nirman.ledger.repository;

import com.nirman.ledger.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findBySiteId(Long siteId);
    List<Expense> findBySiteIdAndDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);
}
