package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Integer>, JpaSpecificationExecutor<FoundItem> {
    @Override
    @EntityGraph(attributePaths = "agency")
    Page<FoundItem> findAll(Specification<FoundItem> specification, Pageable pageable);

    @EntityGraph(attributePaths = "agency")
    List<FoundItem> findByCreatedAtBetweenOrderByAtcIdDesc(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
