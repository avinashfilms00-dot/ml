package com.nirman.ledger.repository;

import com.nirman.ledger.model.Advance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, Long> {
    List<Advance> findByWorkerIdAndDateBetween(Long workerId, LocalDate startDate, LocalDate endDate);
    List<Advance> findBySiteIdAndDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);
    List<Advance> findBySiteId(Long siteId);
}
