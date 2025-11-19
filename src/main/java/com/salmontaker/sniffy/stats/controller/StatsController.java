package com.salmontaker.sniffy.stats.controller;

import com.salmontaker.sniffy.stats.dto.response.FoundItemTotalsResponse;
import com.salmontaker.sniffy.stats.dto.response.Top5AgenciesResponse;
import com.salmontaker.sniffy.stats.dto.response.Top5CategoriesResponse;
import com.salmontaker.sniffy.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/found-items")
    public FoundItemTotalsResponse getFoundItemTotals() {
        return statsService.getFoundItemTotals();
    }

    @GetMapping("/top5-agencies")
    public List<Top5AgenciesResponse> getTop5Agencies() {
        return statsService.getTop5Agencies();
    }

    @GetMapping("/top5-categories")
    public List<Top5CategoriesResponse> getTop5Categories() {
        return statsService.getTop5Categories();
    }
}
