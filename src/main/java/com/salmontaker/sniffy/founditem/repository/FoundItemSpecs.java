package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.dto.internal.request.FoundItemRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class FoundItemSpecs {
    public static Specification<FoundItem> withFilter(FoundItemRequest request) {
        Specification<FoundItem> spec = (root, query, cb) -> cb.conjunction();

        if (request.getId() != null) {
            spec = spec.and(idEq(request.getId()));
        }
        if (StringUtils.hasText(request.getAgencyName())) {
            spec = spec.and(agencyNameContains(request.getAgencyName()));
        }
        if (StringUtils.hasText(request.getClrNm())) {
            spec = spec.and(clrNmContains(request.getClrNm()));
        }
        if (StringUtils.hasText(request.getFdPrdtNm())) {
            spec = spec.and(fdPrdtNmContains(request.getFdPrdtNm()));
        }
        if (StringUtils.hasText(request.getPrdtClNm())) {
            spec = spec.and(prdtClNmContains(request.getPrdtClNm()));
        }
        if (request.getStartDate() != null || request.getEndDate() != null) {
            spec = spec.and(fdYmdBetween(request.getStartDate(), request.getEndDate()));
        }

        return spec;
    }
    
    private static Specification<FoundItem> idEq(Integer id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    private static Specification<FoundItem> agencyNameContains(String agencyName) {
        return (root, query, cb) -> {
            Join<FoundItem, Agency> agencyJoin = root.join("agency", JoinType.LEFT);
            return cb.like(agencyJoin.get("name"), "%" + agencyName + "%");
        };
    }

    private static Specification<FoundItem> clrNmContains(String clrNm) {
        return (root, query, cb) -> cb.like(root.get("clrNm"), "%" + clrNm + "%");
    }

    private static Specification<FoundItem> fdPrdtNmContains(String fdPrdtNm) {
        return (root, query, cb) -> cb.like(root.get("fdPrdtNm"), "%" + fdPrdtNm + "%");
    }

    private static Specification<FoundItem> prdtClNmContains(String prdtClNm) {
        return (root, query, cb) -> cb.like(root.get("prdtClNm"), "%" + prdtClNm + "%");
    }

    private static Specification<FoundItem> fdYmdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("fdYmd"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("fdYmd"), startDate);
            } else {
                return cb.lessThanOrEqualTo(root.get("fdYmd"), endDate);
            }
        };
    }
}