package com.nirman.ledger.repository;

import com.nirman.ledger.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByWorkerIdAndDate(Long workerId, LocalDate date);
    List<Attendance> findBySiteIdAndDate(Long siteId, LocalDate date);
    List<Attendance> findBySiteIdAndDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);
    List<Attendance> findByWorkerIdAndDateBetween(Long workerId, LocalDate startDate, LocalDate endDate);
}
