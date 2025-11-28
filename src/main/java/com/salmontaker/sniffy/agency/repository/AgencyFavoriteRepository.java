package com.salmontaker.sniffy.agency.repository;

import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencyFavoriteRepository extends JpaRepository<AgencyFavorite, Integer> {
    @EntityGraph(attributePaths = "agency")
    Page<AgencyFavorite> findWithAgencyByUserId(Integer userId, Pageable pageable);

    boolean existsByUserIdAndAgencyId(Integer userId, Integer agencyId);

    @Query("SELECT af.agency.id FROM AgencyFavorite af WHERE af.user.id = :userId")
    List<Integer> findAgencyIdByUserId(Integer userId);

    void deleteByUserIdAndAgencyId(Integer userId, Integer agencyId);
}
