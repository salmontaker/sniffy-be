package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Integer>, JpaSpecificationExecutor<FoundItem> {
    @Override
    @EntityGraph(attributePaths = "agency")
    Page<FoundItem> findAll(Specification<FoundItem> specification, Pageable pageable);

    @EntityGraph(attributePaths = "agency")
    @Query("""
            SELECT f FROM FoundItem f
            WHERE f.createdAt >= :startOfToday
            ORDER BY f.atcId DESC
            """)
    List<FoundItem> findToday(@Param("startOfToday") LocalDateTime startOfToday);
}
