package com.nirman.ledger.repository;

import com.nirman.ledger.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findBySiteId(Long siteId);
    List<Worker> findBySiteIdAndActive(Long siteId, boolean active);
}
