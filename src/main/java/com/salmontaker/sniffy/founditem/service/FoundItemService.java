package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.common.PageResponse;
import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.dto.internal.request.FoundItemRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemService {
    private final FoundItemRepository foundItemRepository;

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
