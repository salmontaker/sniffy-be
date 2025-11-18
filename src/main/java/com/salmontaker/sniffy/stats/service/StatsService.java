package com.salmontaker.sniffy.stats.service;

import com.salmontaker.sniffy.stats.dto.response.FoundItemTotalsResponse;
import com.salmontaker.sniffy.stats.dto.response.Top5AgenciesResponse;
import com.salmontaker.sniffy.stats.dto.response.Top5CategoriesResponse;
import com.salmontaker.sniffy.stats.mapper.StatsMapper;
import com.salmontaker.sniffy.stats.mapper.projection.FoundItemTotalsProjection;
import com.salmontaker.sniffy.stats.mapper.projection.Top5AgenciesProjection;
import com.salmontaker.sniffy.stats.mapper.projection.Top5CategoriesProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsMapper statsMapper;

    public FoundItemTotalsResponse getFoundItemTotals() {
        LocalDate today = LocalDate.now();
        LocalDate week = today.with(DayOfWeek.MONDAY); // 이번 주 월요일
        LocalDate month = today.with(TemporalAdjusters.firstDayOfMonth()); // 이번 달 1일

        FoundItemTotalsProjection projection = statsMapper.foundItemTotals(today, week, month);
        return FoundItemTotalsResponse.from(projection);
    }

    public List<Top5AgenciesResponse> getTop5Agencies() {
        List<Top5AgenciesProjection> projection = statsMapper.top5Agencies();
        return projection.stream().map(Top5AgenciesResponse::from).toList();
    }

    public List<Top5CategoriesResponse> getTop5Categories() {
        List<Top5CategoriesProjection> projection = statsMapper.top5Categories();
        return projection.stream().map(Top5CategoriesResponse::from).toList();
    }
}
