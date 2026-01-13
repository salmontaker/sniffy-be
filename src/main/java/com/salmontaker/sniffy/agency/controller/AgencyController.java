package com.salmontaker.sniffy.agency.controller;

import com.salmontaker.sniffy.agency.dto.request.AgencySearchRequest;
import com.salmontaker.sniffy.agency.dto.response.AgencyResponse;
import com.salmontaker.sniffy.agency.service.AgencyService;
import com.salmontaker.sniffy.common.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/agencies")
@RequiredArgsConstructor
public class AgencyController {
    private final AgencyService agencyService;

    @GetMapping("/{agencyId}")
    public AgencyResponse getAgency(@AuthenticationPrincipal Integer userId,
                                    @PathVariable Integer agencyId) {
        return agencyService.getAgency(userId, agencyId);
    }

    @GetMapping
    public List<AgencyResponse> getAgencies(@AuthenticationPrincipal Integer userId,
                                            @ModelAttribute AgencySearchRequest request) {
        return agencyService.getAgencies(userId, request);
    }

    @GetMapping("/favorites")
    public PageResponse<AgencyResponse> getFavoriteAgencies(@AuthenticationPrincipal Integer userId,
                                                            @PageableDefault Pageable pageable) {
        return agencyService.getFavoriteAgencies(userId, pageable);
    }

    @PostMapping("/favorites/{agencyId}")
    public void addFavorite(@AuthenticationPrincipal Integer userId,
                            @PathVariable Integer agencyId) {
        agencyService.addFavorite(userId, agencyId);
    }

    @DeleteMapping("/favorites/{agencyId}")
    public void removeFavorite(@AuthenticationPrincipal Integer userId,
                               @PathVariable Integer agencyId) {
        agencyService.removeFavorite(userId, agencyId);
    }
}
