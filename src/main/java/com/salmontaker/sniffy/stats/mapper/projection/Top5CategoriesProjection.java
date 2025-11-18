package com.salmontaker.sniffy.stats.mapper.projection;

import lombok.Data;

@Data
public class Top5CategoriesProjection {
    private String category;
    private Integer todayTotal;
}
