package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.common.dto.response.OpenApiResponse;
import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import com.salmontaker.sniffy.founditem.repository.FoundItemBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
    private static final int RETRY_COUNT = 3;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Transactional
    public void syncExternalData() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new RuntimeException("syncExternalData is already running.");
        }

        try {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(6);

            String startDate = start.format(DateTimeFormatter.BASIC_ISO_DATE);
            String endDate = end.format(DateTimeFormatter.BASIC_ISO_DATE);

            if (foundItemBatchRepository.hasTodayChangedItems()) {
                return;
            }

            log.info("Fetching TotalCounts...");

            // 1. 임시 테이블 생성 (트랜잭션 시작)
            foundItemBatchRepository.createTempTable();

            try {
                // 2. 비동기 Flux 파이프라인 정의 (아직 실행 안 됨)
                Flux<LostFoundResponse> itemFlux = fetchAllItems(startDate, endDate);

                // 3. Flux를 청크(List<Item>) 단위의 '블로킹' Iterable로 변환
                Iterable<List<LostFoundResponse>> itemChunks = itemFlux.buffer(NUM_OF_ROWS)
                        .toIterable();

                int totalInserted = 0;

                // 4. 청크 단위로 반복하며 DB에 삽입
                for (List<LostFoundResponse> chunk : itemChunks) {
                    if (chunk.isEmpty()) {
                        continue;
                    }

                    foundItemBatchRepository.insertTempTable(chunk); // 청크 단위로 BATCH INSERT
                    totalInserted += chunk.size();
                    log.info("Inserted chunk of {}, total inserted: {}", chunk.size(), totalInserted);
                }

                // 5. 모든 삽입이 끝나면 Merge 수행
                foundItemBatchRepository.mergeToMainTable();

                // 6. 주인을 찾았거나 6개월이 지난 항목 soft delete
                foundItemBatchRepository.deleteFoundOrExpiredItem();
            } catch (Exception e) {
                // 7. 예외 발생시 롤백
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("Sync failed: {}", e.getMessage());
                return;
            } finally {
                // 8. 성공/실패 여부와 관계없이 임시 테이블 삭제
                foundItemBatchRepository.dropTempTable();
            }

            log.info("Sync Completed");
            // (메서드 종료 시 트랜잭션 커밋)
        } finally {
            isRunning.set(false);
        }
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
                .delayElements(Duration.ofSeconds(20))
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
        return Retry.backoff(RETRY_COUNT, Duration.ofSeconds(15))
                .doBeforeRetry(signal ->
                        log.warn("Retrying {} (attempt #{}) due to {}",
                                context,
                                signal.totalRetries() + 1,
                                signal.failure().getMessage()));
    }
}
