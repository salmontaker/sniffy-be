package com.salmontaker.sniffy.founditem.service;

import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import com.salmontaker.sniffy.founditem.repository.FoundItemJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemService {
    private final FoundItemClient foundItemClient;
    private final FoundItemJdbcRepository jdbcRepository;

    private static final int NUM_OF_ROWS = 50000;
    private static final int CONCURRENCY = 3;

    @Transactional
    public void syncExternalData(String startDate, String endDate) {
        log.info("Fetching TotalCounts...");

        fetchAllItems(startDate, endDate)
                .collectList()
                .doOnNext(this::saveToDatabase)
                .doOnTerminate(() -> log.info("Sync Completed"))
                .block();
    }

    private Flux<LostFoundResponse.Item> fetchAllItems(String startDate, String endDate) {
        return fetchTotalCount(startDate, endDate)
                .flatMapMany(totalPages -> fetchAllPages(startDate, endDate, totalPages))
                .flatMapIterable(LostFoundResponse::getItems);
    }

    private Mono<Integer> fetchTotalCount(String startDate, String endDate) {
        return foundItemClient.fetchData(startDate, endDate, 1, 1)
                .flatMap(first -> {
                    if (!first.isSuccess()) {
                        return Mono.error(new RuntimeException("LOST112: " + first.getHeader().getResultMsg()));
                    }

                    int total = first.getBody().getTotalCount();
                    int totalPages = (int) Math.ceil((double) total / NUM_OF_ROWS);
                    log.info("TotalCount={}, TotalPages={}", total, totalPages);
                    return Mono.just(totalPages);
                });
    }

    private Flux<LostFoundResponse> fetchAllPages(String startDate, String endDate, int totalPages) {
        return Flux.range(1, totalPages)
                .delayElements(Duration.ofMillis(250))
                .flatMapSequential(page -> foundItemClient.fetchData(startDate, endDate, page, NUM_OF_ROWS)
                        .doOnSubscribe(sub -> log.info("HTTP call started for page {}", page))
                        .filter(LostFoundResponse::isSuccess), CONCURRENCY);
    }

    private void saveToDatabase(List<LostFoundResponse.Item> items) {
        log.info("Collected {} items", items.size());

        jdbcRepository.createTempTable();
        jdbcRepository.insertTempTable(items);
        jdbcRepository.mergeToMainTable();
        jdbcRepository.dropTempTable();
    }
}
