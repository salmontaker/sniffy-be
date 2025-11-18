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
import org.springframework.data.domain.Pageable;
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
        Page<FoundItem> foundItems = foundItemRepository.findAll(FoundItemSpecs.withFilter(request), pageable);
        return PageResponse.from(foundItems.map(FoundItemResponse::from));
    }

    public List<FoundItemResponse> getRandomTodayItems() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<FoundItem> todayItems = foundItemRepository.findByCreatedAtBetweenOrderByAtcIdDesc(start, end);
        Collections.shuffle(todayItems);

        return todayItems.stream()
                .limit(6)
                .map(FoundItemResponse::from)
                .toList();
    }
}
