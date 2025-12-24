package com.salmontaker.sniffy.push.service;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionDeleteRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionRequest;
import com.salmontaker.sniffy.push.exception.PushSubscriptionNotFoundException;
import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.exception.UserNotFoundException;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {
    private final UserRepository userRepository;
    private final PushSubscriptionRepository pushSubRepository;

    public Boolean checkSubscription(Integer userId, String endpoint) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return pushSubRepository.existsByUserIdAndEndpoint(user.getId(), endpoint);
    }

    @Transactional
    public void subscribe(Integer userId, PushSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String endpoint = request.getEndpoint();
        String p256dh = request.getKeys().getP256dh();
        String auth = request.getKeys().getAuth();

        // 브라우저 프로필당 endpoint는 하나이므로, 겹치는 엔드포인트는 구독시 사용자 교체
        pushSubRepository.findByEndpoint(endpoint)
                .ifPresentOrElse(sub -> sub.update(user, endpoint, p256dh, auth),
                        () -> pushSubRepository.save(PushSubscription.create(user, endpoint, p256dh, auth)));
    }

    @Transactional
    public void unsubscribe(Integer userId, PushSubscriptionDeleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String endpoint = request.getEndpoint();

        PushSubscription subscription = pushSubRepository.findByEndpoint(endpoint)
                .orElseThrow(PushSubscriptionNotFoundException::new);

        // 구독 정보 제거
        pushSubRepository.delete(subscription);
    }
}
