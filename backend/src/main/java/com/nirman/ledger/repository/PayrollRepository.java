package com.nirman.ledger.repository;

import com.nirman.ledger.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findBySiteIdAndStartDateAndEndDate(Long siteId, LocalDate startDate, LocalDate endDate);
    Optional<Payroll> findByWorkerIdAndStartDateAndEndDate(Long workerId, LocalDate startDate, LocalDate endDate);
    List<Payroll> findBySiteId(Long siteId);
}
