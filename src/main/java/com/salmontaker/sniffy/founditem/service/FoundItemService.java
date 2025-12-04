package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.common.OpenApiResponse;
import com.salmontaker.sniffy.common.PageResponse;
import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundDetailResponse;
import com.salmontaker.sniffy.founditem.dto.internal.request.FoundItemRequest;
import com.salmontaker.sniffy.founditem.dto.internal.response.FoundItemDetailResponse;
import com.salmontaker.sniffy.founditem.dto.internal.response.FoundItemResponse;
import com.salmontaker.sniffy.founditem.repository.FoundItemRepository;
import com.salmontaker.sniffy.founditem.repository.FoundItemSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemService {
    private final FoundItemRepository foundItemRepository;
    private final FoundItemClient foundItemClient;

    public PageResponse<FoundItemResponse> getFoundItems(FoundItemRequest request, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "atcId"));
        }
        Page<FoundItem> foundItems = foundItemRepository.findAll(FoundItemSpecs.withFilter(request), pageable);
        return PageResponse.from(foundItems.map(FoundItemResponse::from));
    }

    public FoundItemDetailResponse getFoundItemDetail(Integer id) {
        FoundItem foundItem = foundItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("습득물을 찾을 수 없습니다."));

        LostFoundDetailResponse response = foundItemClient.fetchItemDetail(foundItem.getAtcId(), foundItem.getFdSn())
                .map(OpenApiResponse::getItem)
                .block();

        if (response == null) {
            throw new NoSuchElementException("습득물을 찾을 수 없습니다.");
        }

        response.setUniq(response.getUniq().replaceFirst("내용", "").strip());

        return FoundItemDetailResponse.from(response);
    }

    public List<FoundItemResponse> getRandomTodayItems() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<FoundItem> todayItems = foundItemRepository.findTodayWithImage(startOfToday);

        Collections.shuffle(todayItems);

        return todayItems.stream()
                .limit(6)
                .map(FoundItemResponse::from)
                .toList();
    }
}
