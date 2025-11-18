package com.salmontaker.sniffy.stats.mapper.projection;

import lombok.Data;

@Data
public class Top5AgenciesProjection {
    private Integer id;
    private String name;
    private Integer todayTotal;
}
