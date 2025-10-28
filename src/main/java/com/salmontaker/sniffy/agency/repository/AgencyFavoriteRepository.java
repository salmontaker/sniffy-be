package com.salmontaker.sniffy.agency.repository;

import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyFavoriteRepository extends JpaRepository<AgencyFavorite, Integer> {
    // userId 로 Favorite 조회 + 연관된 Agency 를 한 번에 fetch
    @EntityGraph(attributePaths = "agency")
    Page<AgencyFavorite> findAllByUserId(Integer userId, Pageable pageable);

    boolean existsByUserIdAndAgencyId(Integer userId, Integer agencyId);

    void deleteByUserIdAndAgencyId(Integer userId, Integer agencyId);
}
