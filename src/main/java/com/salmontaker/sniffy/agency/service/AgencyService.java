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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyService {
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final AgencyFavoriteRepository agencyFavoriteRepository;

    public AgencyResponse getAgency(Integer userId, Integer agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NoSuchElementException("센터를 찾을 수 없습니다."));

        boolean isFavorite = false;
        if (userId != null) {
            isFavorite = agencyFavoriteRepository.existsByUserIdAndAgencyId(userId, agencyId);
        }

        return AgencyResponse.from(agency, isFavorite);
    }

    public List<AgencyResponse> getAgencies(Integer userId, AgencySearchRequest request) {
        List<Agency> agencies = agencyRepository.findAllByLatitudeBetweenAndLongitudeBetween(
                request.getMinLatitude(),
                request.getMaxLatitude(),
                request.getMinLongitude(),
                request.getMaxLongitude());

        Set<Integer> favoriteIds = (userId == null)
                ? Set.of()
                : Set.copyOf(agencyFavoriteRepository.findAgencyIdByUserId(userId));

        return agencies.stream()
                .map(agency -> AgencyResponse.from(agency, favoriteIds.contains(agency.getId())))
                .toList();
    }

    public PageResponse<AgencyResponse> getFavoriteAgencies(Integer userId, Pageable pageable) {
        Page<AgencyFavorite> agencyFavorites = agencyFavoriteRepository.findWithAgencyByUserId(userId, pageable);
        return PageResponse.from(agencyFavorites.map(favorite -> AgencyResponse.from(favorite.getAgency(), true)));
    }

    @Transactional
    public void addFavorite(Integer userId, Integer agencyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NoSuchElementException("센터를 찾을 수 없습니다."));

        AgencyFavorite newFavorite = AgencyFavorite.create(user, agency);
        agencyFavoriteRepository.save(newFavorite);
    }

    @Transactional
    public void removeFavorite(Integer userId, Integer agencyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NoSuchElementException("센터를 찾을 수 없습니다."));

        agencyFavoriteRepository.deleteByUserIdAndAgencyId(userId, agencyId);
    }
}
