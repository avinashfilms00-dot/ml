package com.nirman.ledger.service;

import com.nirman.ledger.dto.SiteRequest;
import com.nirman.ledger.dto.SiteResponse;
import com.nirman.ledger.model.Role;
import com.nirman.ledger.model.Site;
import com.nirman.ledger.model.SiteStatus;
import com.nirman.ledger.model.User;
import com.nirman.ledger.repository.SiteRepository;
import com.nirman.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;

    @Transactional
    public SiteResponse createSite(SiteRequest request, User contractor) {
        Optional<User> ownerUser = userRepository.findByMobile(request.getOwnerMobile())
                .filter(u -> u.getRole() == Role.ROLE_OWNER);

        Site site = Site.builder()
                .siteName(request.getSiteName())
                .address(request.getAddress())
                .ownerName(request.getOwnerName())
                .ownerMobile(request.getOwnerMobile())
                .startDate(request.getStartDate())
                .status(request.getStatus() != null ? request.getStatus() : SiteStatus.ACTIVE)
                .contractor(contractor)
                .owner(ownerUser.orElse(null))
                .build();

        Site savedSite = siteRepository.save(site);
        return mapToResponse(savedSite);
    }

    public List<SiteResponse> getAllSites(User user) {
        List<Site> sites;
        if (user.getRole() == Role.ROLE_CONTRACTOR) {
            sites = siteRepository.findByContractorId(user.getId());
        } else {
            sites = siteRepository.findByOwnerId(user.getId());
            if (sites.isEmpty()) {
                sites = siteRepository.findByOwnerMobile(user.getMobile());
                for (Site s : sites) {
                    if (s.getOwner() == null) {
                        s.setOwner(user);
                        siteRepository.save(s);
                    }
                }
            }
        }
        return sites.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public SiteResponse getSiteById(Long id, User user) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (user.getRole() == Role.ROLE_CONTRACTOR && !site.getContractor().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access to this site");
        }
        if (user.getRole() == Role.ROLE_OWNER && !site.getOwnerMobile().equals(user.getMobile()) 
                && (site.getOwner() == null || !site.getOwner().getId().equals(user.getId()))) {
            throw new SecurityException("Unauthorized access to this site");
        }

        return mapToResponse(site);
    }

    @Transactional
    public SiteResponse updateSite(Long id, SiteRequest request, User contractor) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to update this site");
        }

        Optional<User> ownerUser = userRepository.findByMobile(request.getOwnerMobile())
                .filter(u -> u.getRole() == Role.ROLE_OWNER);

        site.setSiteName(request.getSiteName());
        site.setAddress(request.getAddress());
        site.setOwnerName(request.getOwnerName());
        site.setOwnerMobile(request.getOwnerMobile());
        site.setStartDate(request.getStartDate());
        if (request.getStatus() != null) {
            site.setStatus(request.getStatus());
        }
        site.setOwner(ownerUser.orElse(null));

        Site updatedSite = siteRepository.save(site);
        return mapToResponse(updatedSite);
    }

    @Transactional
    public void deleteSite(Long id, User contractor) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));

        if (!site.getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Unauthorized to delete this site");
        }

        siteRepository.delete(site);
    }

    public SiteResponse mapToResponse(Site site) {
        return SiteResponse.builder()
                .id(site.getId())
                .siteName(site.getSiteName())
                .address(site.getAddress())
                .ownerName(site.getOwnerName())
                .ownerMobile(site.getOwnerMobile())
                .startDate(site.getStartDate())
                .status(site.getStatus())
                .contractorId(site.getContractor().getId())
                .ownerId(site.getOwner() != null ? site.getOwner().getId() : null)
                .build();
    }
}
