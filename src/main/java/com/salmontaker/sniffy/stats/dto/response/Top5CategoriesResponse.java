package com.salmontaker.sniffy.stats.dto.response;

import com.salmontaker.sniffy.stats.mapper.projection.Top5CategoriesProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Top5CategoriesResponse {
    private String category;
    private Integer todayTotal;

    public static Top5CategoriesResponse from(Top5CategoriesProjection projection) {
        return Top5CategoriesResponse.builder()
                .category(projection.getCategory())
                .todayTotal(projection.getTodayTotal())
                .build();
    }
}
