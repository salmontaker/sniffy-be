package com.salmontaker.sniffy.stats.mapper.projection;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoundItemTotalsProjection {
    private Integer todayTotal;
    private Integer weekTotal;
    private Integer monthTotal;
    private LocalDateTime lastUpdated;
}
