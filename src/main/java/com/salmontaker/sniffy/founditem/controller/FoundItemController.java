package com.salmontaker.sniffy.founditem.controller;

import com.salmontaker.sniffy.common.PageResponse;
import com.salmontaker.sniffy.founditem.dto.internal.request.FoundItemRequest;
import com.salmontaker.sniffy.founditem.dto.internal.response.FoundItemResponse;
import com.salmontaker.sniffy.founditem.service.FoundItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/found-items")
@RequiredArgsConstructor
public class FoundItemController {
    private final FoundItemService foundItemService;

    @GetMapping
    public PageResponse<FoundItemResponse> getFoundItems(@ModelAttribute FoundItemRequest request,
                                                         @PageableDefault Pageable pageable) {
        return foundItemService.getFoundItems(request, pageable);
    }

    @GetMapping("/samples")
    public List<FoundItemResponse> getRandomTodayItems() {
        return foundItemService.getRandomTodayItems();
    }
}
