package com.salmontaker.sniffy.push.service;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionDeleteRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionRequest;
import com.salmontaker.sniffy.push.dto.response.PushSubscriptionResponse;
import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {
    private final UserRepository userRepository;
    private final PushSubscriptionRepository subscriptionRepository;

    @Transactional
    public PushSubscriptionResponse subscribe(Integer userId, PushSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String endpoint = request.getEndpoint();
        String p256dh = request.getKeys().getP256dh();
        String auth = request.getKeys().getAuth();

        PushSubscription subscription = subscriptionRepository.findByEndpoint(endpoint)
                .orElse(PushSubscription.create(user, endpoint, p256dh, auth));

        return PushSubscriptionResponse.from(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void unsubscribe(Integer userId, PushSubscriptionDeleteRequest request) {
        String endpoint = request.getEndpoint();

        PushSubscription subscription = subscriptionRepository.findByEndpoint(endpoint)
                .orElseThrow(() -> new NoSuchElementException("구독정보를 찾을 수 없습니다."));

        Integer subscriptionOwnerId = subscription.getUser().getId();

        if (!userId.equals(subscriptionOwnerId)) {
            throw new AccessDeniedException("해당 사용자의 구독정보가 아닙니다.");
        }

        subscriptionRepository.delete(subscription);
    }
}
