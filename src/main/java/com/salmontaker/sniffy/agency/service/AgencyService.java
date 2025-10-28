package com.salmontaker.sniffy.agency.service;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import com.salmontaker.sniffy.agency.dto.request.AgencySearchRequest;
import com.salmontaker.sniffy.agency.repository.AgencyFavoriteRepository;
import com.salmontaker.sniffy.agency.repository.AgencyRepository;
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

    public Agency getAgency(Integer id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
    }

    public List<Agency> getAgencies(AgencySearchRequest request) {
        return agencyRepository.findAllByLatitudeBetweenAndLongitudeBetween(
                request.getMinLatitude(),
                request.getMaxLatitude(),
                request.getMinLongitude(),
                request.getMaxLongitude());
    }

    public Page<Agency> getFavoriteAgencies(Integer userId, Pageable pageable) {
        Page<AgencyFavorite> agencyFavorites = agencyFavoriteRepository.findAllByUserId(userId, pageable);
        return agencyFavorites.map(AgencyFavorite::getAgency);
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
