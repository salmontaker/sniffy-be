package com.salmontaker.sniffy.agency.controller;

import com.salmontaker.sniffy.agency.dto.request.AgencySearchRequest;
import com.salmontaker.sniffy.agency.dto.response.AgencyResponse;
import com.salmontaker.sniffy.agency.service.AgencyService;
import com.salmontaker.sniffy.common.PageResponse;
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

    @GetMapping("/{id}")
    public AgencyResponse getAgency(@PathVariable Integer id) {
        return agencyService.getAgency(id);
    }

    @GetMapping
    public List<AgencyResponse> getAgencies(@ModelAttribute AgencySearchRequest request) {
        return agencyService.getAgencies(request);
    }

    @GetMapping("/favorites")
    public PageResponse<AgencyResponse> getFavoriteAgencies(@AuthenticationPrincipal Integer userId,
                                                            @PageableDefault Pageable pageable) {
        return agencyService.getFavoriteAgencies(userId, pageable);
    }

    @PostMapping("/favorites/{agencyId}")
    public Boolean postToggleFavorite(@AuthenticationPrincipal Integer userId,
                                      @PathVariable Integer agencyId) {
        return agencyService.toggleFavorite(userId, agencyId);
    }
}
