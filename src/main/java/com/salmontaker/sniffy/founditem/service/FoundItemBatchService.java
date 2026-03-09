package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.common.dto.response.OpenApiResponse;
import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import com.salmontaker.sniffy.founditem.repository.FoundItemBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemBatchService {
    private final FoundItemClient foundItemClient;
    private final FoundItemBatchRepository foundItemBatchRepository;

    private static final int NUM_OF_ROWS = 50000;

    private static final int CONCURRENCY = 3;
    private static final int CONCURRENCY_DELAY = 20;

    private static final int RETRY_COUNT = 3;
    private static final int RETRY_DELAY = 15;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void syncExternalData() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new RuntimeException("syncExternalData is already running.");
        }

        try {
            if (foundItemBatchRepository.hasTodayChangedItems()) {
                return;
            }

            // 스테이징 테이블 초기화 후, 스트리밍으로 API 데이터 삽입
            fetchAndStageItems();

            // 스테이징 테이블과 메인 테이블 머지
            foundItemBatchRepository.mergeAndSync();

            log.info("Sync Completed");
        } finally {
            isRunning.set(false);
        }
    }

    private void fetchAndStageItems() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(6);

        String startDate = start.format(DateTimeFormatter.BASIC_ISO_DATE);
        String endDate = end.format(DateTimeFormatter.BASIC_ISO_DATE);

        foundItemBatchRepository.recreateStagingTable();

        log.info("Fetching all items from external API...");

        Iterable<List<LostFoundResponse>> itemChunks = fetchAllItems(startDate, endDate)
                .buffer(NUM_OF_ROWS)
                .toIterable();

        int totalInserted = 0;

        for (List<LostFoundResponse> chunk : itemChunks) {
            if (chunk.isEmpty()) {
                continue;
            }

            foundItemBatchRepository.insertStagingTable(chunk);
            totalInserted += chunk.size();
            log.info("Inserted chunk of {}, total inserted: {}", chunk.size(), totalInserted);
        }

        log.info("Fetched {} items. Starting DB sync...", totalInserted);
    }

    private Flux<LostFoundResponse> fetchAllItems(String startDate, String endDate) {
        return fetchTotalCount(startDate, endDate)
                .flatMapMany(totalPages -> fetchAllPages(startDate, endDate, totalPages))
                .flatMapIterable(OpenApiResponse::getItems);
    }

    private Mono<Integer> fetchTotalCount(String startDate, String endDate) {
        return foundItemClient.fetchItemList(startDate, endDate, 1, 1)
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        return Mono.error(new RuntimeException("LOST112: " + response.getHeader().getResultMsg()));
                    }

                    int total = response.getBody().getTotalCount();
                    int totalPages = (int) Math.ceil((double) total / NUM_OF_ROWS);

                    log.info("TotalCount={}, TotalPages={}", total, totalPages);

                    return Mono.just(totalPages);
                })
                .retryWhen(defaultRetry("fetchTotalCount"));
    }

    private Flux<OpenApiResponse<LostFoundResponse>> fetchAllPages(String startDate, String endDate, int totalPages) {
        return Flux.range(1, totalPages)
                .delayElements(Duration.ofSeconds(CONCURRENCY_DELAY))
                .flatMap(page -> foundItemClient.fetchItemList(startDate, endDate, page, NUM_OF_ROWS)
                                .flatMap(response -> {
                                    if (!response.isSuccess()) {
                                        return Mono.error(new RuntimeException("LOST112: " + response.getHeader().getResultMsg()));
                                    }
                                    return Mono.just(response);
                                })
                                .doOnSubscribe(sub -> log.info("Requesting page {}", page))
                                .retryWhen(defaultRetry("page " + page)),
                        CONCURRENCY);
    }

    private Retry defaultRetry(String context) {
        return Retry.backoff(RETRY_COUNT, Duration.ofSeconds(RETRY_DELAY))
                .doBeforeRetry(signal ->
                        log.warn("Retrying {} (attempt #{}) due to {}",
                                context,
                                signal.totalRetries() + 1,
                                signal.failure().getMessage()));
    }
}
