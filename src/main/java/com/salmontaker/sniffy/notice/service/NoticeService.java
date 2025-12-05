package com.salmontaker.sniffy.notice.service;

import com.salmontaker.sniffy.common.PageResponse;
import com.salmontaker.sniffy.notice.domain.Notice;
import com.salmontaker.sniffy.notice.dto.response.NoticeResponse;
import com.salmontaker.sniffy.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    
    public PageResponse<NoticeResponse> getNotices(Integer userId, Pageable pageable) {
        Page<Notice> notices = noticeRepository.findByUserId(userId, pageable);
        return PageResponse.from(notices.map(NoticeResponse::from));
    }

    @Transactional
    public void deleteNotice(Integer userId, Integer noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoSuchElementException("알림을 찾을 수 없습니다."));

        Integer noticeOwnerId = notice.getUser().getId();

        if (!userId.equals(noticeOwnerId)) {
            throw new AccessDeniedException("해당 유저의 알림이 아닙니다.");
        }

        noticeRepository.delete(notice);
    }
}
