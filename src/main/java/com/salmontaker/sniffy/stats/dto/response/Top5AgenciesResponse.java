package com.salmontaker.sniffy.stats.dto.response;

import com.salmontaker.sniffy.stats.mapper.projection.Top5AgenciesProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Top5AgenciesResponse {
    private Integer id;
    private String name;
    private Integer todayTotal;

    public static Top5AgenciesResponse from(Top5AgenciesProjection projection) {
        return Top5AgenciesResponse.builder()
                .id(projection.getId())
                .name(projection.getName())
                .todayTotal(projection.getTodayTotal())
                .build();
    }
}