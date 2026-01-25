package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.common.dto.response.PageResponse;
import com.salmontaker.sniffy.common.exception.ExternalApiException;
import com.salmontaker.sniffy.founditem.domain.FoundItem;
import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundDetailResponse;
import com.salmontaker.sniffy.founditem.dto.internal.request.FoundItemRequest;
import com.salmontaker.sniffy.founditem.dto.internal.response.FoundItemDetailResponse;
import com.salmontaker.sniffy.founditem.dto.internal.response.FoundItemResponse;
import com.salmontaker.sniffy.founditem.exception.FoundItemNotFoundException;
import com.salmontaker.sniffy.founditem.repository.FoundItemRepository;
import com.salmontaker.sniffy.founditem.repository.FoundItemSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemService {
    private final FoundItemRepository foundItemRepository;
    private final FoundItemClient foundItemClient;

    public PageResponse<FoundItemResponse> getFoundItems(FoundItemRequest request, Pageable pageable) {
        Page<FoundItem> foundItems = foundItemRepository.findAll(FoundItemSpecs.withFilter(request), pageable);
        return PageResponse.from(foundItems.map(FoundItemResponse::from));
    }

    public FoundItemDetailResponse getFoundItemDetail(Integer id) {
        FoundItem foundItem = foundItemRepository.findById(id)
                .orElseThrow(FoundItemNotFoundException::new);

        return foundItemClient.fetchItemDetail(foundItem.getAtcId(), foundItem.getFdSn())
                .flatMap(res -> {
                    if (!res.isSuccess() || res.getItem() == null) {
                        return Mono.error(new FoundItemNotFoundException());
                    }

                    LostFoundDetailResponse item = res.getItem();
                    item.setUniq(item.getUniq().replaceFirst("내용", "").strip());

                    return Mono.just(FoundItemDetailResponse.from(item));
                })
                .block();
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
