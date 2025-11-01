package com.salmontaker.sniffy.agency.service;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import com.salmontaker.sniffy.agency.dto.request.AgencySearchRequest;
import com.salmontaker.sniffy.agency.dto.response.AgencyResponse;
import com.salmontaker.sniffy.agency.repository.AgencyFavoriteRepository;
import com.salmontaker.sniffy.agency.repository.AgencyRepository;
import com.salmontaker.sniffy.common.PageResponse;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyService {
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final AgencyFavoriteRepository agencyFavoriteRepository;

    public AgencyResponse getAgency(Integer id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));

        return AgencyResponse.from(agency);
    }

    public List<AgencyResponse> getAgencies(AgencySearchRequest request) {
        List<Agency> agencies = agencyRepository.findAllByLatitudeBetweenAndLongitudeBetween(
                request.getMinLatitude(),
                request.getMaxLatitude(),
                request.getMinLongitude(),
                request.getMaxLongitude());

        return agencies.stream()
                .map(AgencyResponse::from)
                .toList();
    }

    public PageResponse<AgencyResponse> getFavoriteAgencies(Integer userId, Pageable pageable) {
        Page<AgencyFavorite> agencyFavorites = agencyFavoriteRepository.findAllByUserId(userId, pageable);
        Page<Agency> agencies = agencyFavorites.map(AgencyFavorite::getAgency);
        
        return PageResponse.from(agencies.map(AgencyResponse::from));
    }

    @Transactional
    public Boolean toggleFavorite(Integer userId, Integer agencyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));

        boolean isFavorite = agencyFavoriteRepository.existsByUserIdAndAgencyId(userId, agencyId);

        if (isFavorite) {
            agencyFavoriteRepository.deleteByUserIdAndAgencyId(userId, agencyId);
            return false;
        } else {
            AgencyFavorite newFavorite = AgencyFavorite.create(user, agency);
            agencyFavoriteRepository.save(newFavorite);
            return true;
        }
    }
}
