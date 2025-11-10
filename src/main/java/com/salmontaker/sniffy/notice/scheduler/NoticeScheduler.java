package com.salmontaker.sniffy.notice.scheduler;

import com.salmontaker.sniffy.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeScheduler {
    private final NoticeService noticeService;

    @Scheduled(cron = "0 0 6 * * *")
    public void createKeywordNotification() {
        noticeService.createKeywordNotification();
    }
}
