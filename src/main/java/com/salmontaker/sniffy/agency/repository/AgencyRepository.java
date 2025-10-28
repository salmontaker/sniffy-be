package com.salmontaker.sniffy.agency.repository;

import com.salmontaker.sniffy.agency.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Integer> {
    List<Agency> findAllByLatitudeBetweenAndLongitudeBetween(
            BigDecimal minLatitude,
            BigDecimal maxLatitude,
            BigDecimal minLongitude,
            BigDecimal maxLongitude);
}
