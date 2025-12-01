package com.salmontaker.sniffy.founditem.scheduler;

import com.salmontaker.sniffy.founditem.service.FoundItemBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoundItemScheduler {
    private final FoundItemBatchService foundItemBatchService;

    @Scheduled(cron = "0 */10 2-4 * * *")
    public void syncExternalData() {
        foundItemBatchService.syncExternalData();
    }
}