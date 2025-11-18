package com.salmontaker.sniffy.stats.mapper;

import com.salmontaker.sniffy.stats.mapper.projection.FoundItemTotalsProjection;
import com.salmontaker.sniffy.stats.mapper.projection.Top5AgenciesProjection;
import com.salmontaker.sniffy.stats.mapper.projection.Top5CategoriesProjection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatsMapper {
    FoundItemTotalsProjection foundItemTotals(@Param("today") LocalDate today,
                                              @Param("week") LocalDate week,
                                              @Param("month") LocalDate month);

    List<Top5AgenciesProjection> top5Agencies();

    List<Top5CategoriesProjection> top5Categories();
}
