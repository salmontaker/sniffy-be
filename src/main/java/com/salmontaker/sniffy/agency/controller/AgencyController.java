package com.salmontaker.sniffy.agency.controller;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.agency.dto.request.AgencySearchRequest;
import com.salmontaker.sniffy.agency.dto.response.AgencyResponse;
import com.salmontaker.sniffy.agency.service.AgencyService;
import com.salmontaker.sniffy.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/agency")
@RequiredArgsConstructor
public class AgencyController {
    private final AgencyService agencyService;

    @GetMapping("/{id}")
    public AgencyResponse getAgency(@PathVariable Integer id) {
        Agency agency = agencyService.getAgency(id);
        return AgencyResponse.from(agency);
    }

    @GetMapping
    public List<AgencyResponse> getAgencies(@ModelAttribute AgencySearchRequest request) {
        List<Agency> agencies = agencyService.getAgencies(request);
        return agencies.stream()
                .map(AgencyResponse::from)
                .toList();
    }

    @GetMapping("/favorite")
    public PageResponse<AgencyResponse> getFavoriteAgencies(@AuthenticationPrincipal Integer userId,
                                                            @PageableDefault Pageable pageable) {
        Page<Agency> agencies = agencyService.getFavoriteAgencies(userId, pageable);
        return PageResponse.from(agencies.map(AgencyResponse::from));
    }

    @PostMapping("/favorite/{agencyId}")
    public Boolean postToggleFavorite(@AuthenticationPrincipal Integer userId,
                                      @PathVariable Integer agencyId) {
        return agencyService.toggleFavorite(userId, agencyId);
    }
}
