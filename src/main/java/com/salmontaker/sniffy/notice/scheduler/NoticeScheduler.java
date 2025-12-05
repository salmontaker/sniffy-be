package com.salmontaker.sniffy.notice.scheduler;

import com.salmontaker.sniffy.notice.service.NoticeCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeScheduler {
    private final NoticeCreateService noticeCreateService;

    @Scheduled(cron = "0 0 6 * * *")
    public void createKeywordNotification() {
        noticeCreateService.createKeywordNotification();
    }
}
