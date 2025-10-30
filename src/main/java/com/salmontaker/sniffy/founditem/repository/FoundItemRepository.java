package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Integer> {
}
