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

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemService {
    private final FoundItemRepository foundItemRepository;

    public PageResponse<FoundItemResponse> getFoundItems(FoundItemRequest request, Pageable pageable) {
        Page<FoundItem> foundItems = foundItemRepository.findAll(FoundItemSpecs.withFilter(request), pageable);
        return PageResponse.from(foundItems.map(FoundItemResponse::from));
    }
}
