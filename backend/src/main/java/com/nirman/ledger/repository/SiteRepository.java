package com.nirman.ledger.repository;

import com.nirman.ledger.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByContractorId(Long contractorId);
    List<Site> findByOwnerId(Long ownerId);
    List<Site> findByOwnerMobile(String ownerMobile);
}
