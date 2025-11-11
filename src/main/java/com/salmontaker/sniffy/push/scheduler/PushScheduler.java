package com.salmontaker.sniffy.push.scheduler;

import com.salmontaker.sniffy.push.service.PushNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushScheduler {
    private final PushNoticeService pushNoticeService;

    @Scheduled(cron = "0 0 9 * * *")
    public void pushNotice() {
        pushNoticeService.pushNotice();
    }
}
