package com.salmontaker.sniffy.push.service;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionDeleteRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionRequest;
import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {
    private final UserRepository userRepository;
    private final PushSubscriptionRepository pushSubRepository;

    public Boolean subExists(Integer userId, String endpoint) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        return pushSubRepository.existsByUserIdAndEndpoint(user.getId(), endpoint);
    }

    @Transactional
    public void subscribe(Integer userId, PushSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String endpoint = request.getEndpoint();
        String p256dh = request.getKeys().getP256dh();
        String auth = request.getKeys().getAuth();

        Optional<PushSubscription> subscription = pushSubRepository.findByEndpoint(endpoint);
        if (subscription.isPresent()) {
            // 다른 사용자여도 같은 브라우저에서 구독하는 경우가 있으므로, 사용자 정보도 교체
            PushSubscription existSub = subscription.get();
            existSub.update(user, endpoint, p256dh, auth);
        } else {
            // 구독이 없을 경우 새로운 구독 생성
            PushSubscription newSub = PushSubscription.create(user, endpoint, p256dh, auth);
            pushSubRepository.save(newSub);
        }
    }

    @Transactional
    public void unsubscribe(Integer userId, PushSubscriptionDeleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String endpoint = request.getEndpoint();

        PushSubscription subscription = pushSubRepository.findByEndpoint(endpoint)
                .orElseThrow(() -> new NoSuchElementException("구독정보를 찾을 수 없습니다."));

        Integer subscriptionOwnerId = subscription.getUser().getId();

        if (!user.getId().equals(subscriptionOwnerId)) {
            throw new AccessDeniedException("해당 사용자의 구독정보가 아닙니다.");
        }

        pushSubRepository.delete(subscription);
    }
}
