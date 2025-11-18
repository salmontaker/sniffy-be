package com.salmontaker.sniffy.stats.dto.response;

import com.salmontaker.sniffy.stats.mapper.projection.FoundItemTotalsProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FoundItemTotalsResponse {
    private Integer todayTotal;
    private Integer weekTotal;
    private Integer monthTotal;
    private LocalDateTime lastUpdated;

    public static FoundItemTotalsResponse from(FoundItemTotalsProjection projection) {
        return FoundItemTotalsResponse.builder()
                .todayTotal(projection.getTodayTotal())
                .weekTotal(projection.getWeekTotal())
                .monthTotal(projection.getMonthTotal())
                .lastUpdated(projection.getLastUpdated())
                .build();
    }
}
