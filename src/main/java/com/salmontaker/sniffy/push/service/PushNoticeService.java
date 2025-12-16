package com.salmontaker.sniffy.push.service;

import com.salmontaker.sniffy.notice.domain.Notice;
import com.salmontaker.sniffy.notice.repository.NoticeRepository;
import com.salmontaker.sniffy.push.domain.PushSubscription;
import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Encoding;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNoticeService {
    private final NoticeRepository noticeRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PushAsyncService pushService;

    @Transactional
    public void pushNotice() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<Notice> notices = noticeRepository.findAllByCreatedAtAfterAndSentAtIsNull(startOfToday);

        if (notices.isEmpty()) {
            return;
        }

        List<Integer> userIds = notices.stream()
                .map(notice -> notice.getUser().getId())
                .distinct()
                .toList();

        Map<Integer, List<PushSubscription>> userSubMap = pushSubscriptionRepository.findAllByUserIdIn(userIds)
                .stream()
                .collect(Collectors.groupingBy(ps -> ps.getUser().getId()));

        List<PushSubscription> invalidSubscriptions = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Notice notice : notices) {
            Integer userId = notice.getUser().getId();
            List<PushSubscription> subscriptions = userSubMap.getOrDefault(userId, Collections.emptyList());

            if (subscriptions.isEmpty()) {
                continue;
            }

            for (PushSubscription subscription : subscriptions) {
                try {
                    Notification notification = createNotification(notice, subscription);
                    CompletableFuture<Void> future = pushService.send(notification, Encoding.AES128GCM)
                            .thenAccept(response -> {
                                int statusCode = response.getStatusCode();
                                if (isSuccess(statusCode)) {
                                    synchronized (notice) {
                                        if (notice.getSentAt() == null) {
                                            notice.sentAt(LocalDateTime.now());
                                        }
                                    }
                                } else if (isInvalidSubscription(statusCode)) {
                                    invalidSubscriptions.add(subscription);
                                }
                            })
                            .exceptionally(e -> {
                                log.error(e.getMessage());
                                return null;
                            });

                    futures.add(future);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        if (!futures.isEmpty()) {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        if (!invalidSubscriptions.isEmpty()) {
            pushSubscriptionRepository.deleteAllInBatch(invalidSubscriptions);
        }
    }

    private boolean isSuccess(int statusCode) {
        return statusCode == 200 || statusCode == 201 || statusCode == 202;
    }

    private boolean isInvalidSubscription(int statusCode) {
        return statusCode == 404 || statusCode == 409 || statusCode == 410;
    }

    private Notification createNotification(Notice notice, PushSubscription subscription) throws Exception {
        return Notification.builder()
                .endpoint(subscription.getEndpoint())
                .userPublicKey(subscription.getP256dh())
                .userAuth(subscription.getAuth())
                .payload(notice.getTitle())
                .build();
    }
}
