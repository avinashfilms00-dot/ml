package com.nirman.ledger.service;

import com.nirman.ledger.dto.AdvanceRequest;
import com.nirman.ledger.dto.AdvanceResponse;
import com.nirman.ledger.model.Advance;
import com.nirman.ledger.model.Site;
import com.nirman.ledger.model.User;
import com.nirman.ledger.model.Worker;
import com.nirman.ledger.repository.AdvanceRepository;
import com.nirman.ledger.repository.SiteRepository;
import com.nirman.ledger.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvanceService {

    private final AdvanceRepository advanceRepository;
    private final SiteRepository siteRepository;
    private final WorkerRepository workerRepository;
    private final SiteService siteService;

    @Transactional
    public AdvanceResponse createAdvance(AdvanceRequest request, User contractor) {
        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!worker.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException("Worker does not belong to specified site");
        }

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to log advances at this site");
        }

        Advance advance = Advance.builder()
                .worker(worker)
                .site(site)
                .amount(request.getAmount())
                .date(request.getDate())
                .note(request.getNote())
                .build();

        Advance saved = advanceRepository.save(advance);
        return mapToResponse(saved);
    }

    public List<AdvanceResponse> getAdvancesBySite(Long siteId, User user) {
        siteService.getSiteById(siteId, user);
        return advanceRepository.findBySiteId(siteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AdvanceResponse mapToResponse(Advance advance) {
        return AdvanceResponse.builder()
                .id(advance.getId())
                .workerId(advance.getWorker().getId())
                .workerName(advance.getWorker().getName())
                .siteId(advance.getSite().getId())
                .siteName(advance.getSite().getSiteName())
                .amount(advance.getAmount())
                .date(advance.getDate())
                .note(advance.getNote())
                .build();
    }
}
