package com.salmontaker.sniffy.founditem.scheduler;

import com.salmontaker.sniffy.founditem.service.FoundItemBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FoundItemScheduler {
    private final FoundItemBatchService foundItemBatchService;

    @Scheduled(cron = "0 */10 2-4 * * *")
    public void syncExternalData() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(6);

        String startDate = start.format(DateTimeFormatter.BASIC_ISO_DATE);
        String endDate = end.format(DateTimeFormatter.BASIC_ISO_DATE);

        foundItemBatchService.syncExternalData(startDate, endDate);
    }
}