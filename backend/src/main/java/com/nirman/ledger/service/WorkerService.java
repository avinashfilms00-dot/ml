package com.nirman.ledger.service;

import com.nirman.ledger.dto.WorkerRequest;
import com.nirman.ledger.dto.WorkerResponse;
import com.nirman.ledger.model.Site;
import com.nirman.ledger.model.User;
import com.nirman.ledger.model.Worker;
import com.nirman.ledger.repository.SiteRepository;
import com.nirman.ledger.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final SiteRepository siteRepository;
    private final SiteService siteService;

    @Transactional
    public WorkerResponse createWorker(WorkerRequest request, User contractor) {
        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to manage workers at this site");
        }

        Worker worker = Worker.builder()
                .name(request.getName())
                .mobile(request.getMobile())
                .skill(request.getSkill())
                .dailyWage(request.getDailyWage())
                .active(request.getActive() != null ? request.getActive() : true)
                .site(site)
                .build();

        Worker savedWorker = workerRepository.save(worker);
        return mapToResponse(savedWorker);
    }

    public List<WorkerResponse> getWorkersBySite(Long siteId, User user) {
        // Site service verifies access
        siteService.getSiteById(siteId, user);
        
        return workerRepository.findBySiteId(siteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkerResponse updateWorker(Long id, WorkerRequest request, User contractor) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        Site site = worker.getSite();
        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to update workers at this site");
        }

        worker.setName(request.getName());
        worker.setMobile(request.getMobile());
        worker.setSkill(request.getSkill());
        worker.setDailyWage(request.getDailyWage());
        if (request.getActive() != null) {
            worker.setActive(request.getActive());
        }

        if (!worker.getSite().getId().equals(request.getSiteId())) {
            Site newSite = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new IllegalArgumentException("Target site not found"));
            if (!newSite.getContractor().getId().equals(contractor.getId())) {
                throw new SecurityException("Unauthorized to assign worker to this target site");
            }
            worker.setSite(newSite);
        }

        Worker updatedWorker = workerRepository.save(worker);
        return mapToResponse(updatedWorker);
    }

    @Transactional
    public void deleteWorker(Long id, User contractor) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        if (!worker.getSite().getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to delete workers at this site");
        }

        workerRepository.delete(worker);
    }

    public WorkerResponse mapToResponse(Worker worker) {
        return WorkerResponse.builder()
                .id(worker.getId())
                .name(worker.getName())
                .mobile(worker.getMobile())
                .skill(worker.getSkill())
                .dailyWage(worker.getDailyWage())
                .active(worker.isActive())
                .siteId(worker.getSite().getId())
                .siteName(worker.getSite().getSiteName())
                .build();
    }
}
