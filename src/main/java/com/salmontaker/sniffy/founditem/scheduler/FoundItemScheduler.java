package com.salmontaker.sniffy.founditem.scheduler;

import com.salmontaker.sniffy.founditem.service.FoundItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FoundItemScheduler {
    private final FoundItemService foundItemService;
    
    @Scheduled(cron = "0 0 4-5 * * *")
    public void syncExternalData() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(6);

        String startDate = start.format(DateTimeFormatter.BASIC_ISO_DATE);
        String endDate = end.format(DateTimeFormatter.BASIC_ISO_DATE);

        foundItemService.syncExternalData(startDate, endDate);
    }
}