package com.salmontaker.sniffy.notice.controller;

import com.salmontaker.sniffy.common.dto.response.PageResponse;
import com.salmontaker.sniffy.notice.dto.response.NoticeResponse;
import com.salmontaker.sniffy.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public PageResponse<NoticeResponse> getNotices(@AuthenticationPrincipal Integer userId,
                                                   @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return noticeService.getNotices(userId, pageable);
    }

    @DeleteMapping("/{noticeId}")
    public void deleteNotice(@AuthenticationPrincipal Integer userId, @PathVariable Integer noticeId) {
        noticeService.deleteNotice(userId, noticeId);
    }
}
