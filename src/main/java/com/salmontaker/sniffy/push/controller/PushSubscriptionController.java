package com.salmontaker.sniffy.push.controller;

import com.salmontaker.sniffy.push.dto.request.PushSubscriptionDeleteRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionVerifyRequest;
import com.salmontaker.sniffy.push.dto.response.PushSubscriptionResponse;
import com.salmontaker.sniffy.push.dto.response.PushSubscriptionVerifyResponse;
import com.salmontaker.sniffy.push.service.PushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/push-subscription")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionService subscriptionService;

    @PostMapping
    public PushSubscriptionResponse subscribe(@AuthenticationPrincipal Integer userId,
                                              @RequestBody PushSubscriptionRequest request) {
        return subscriptionService.subscribe(userId, request);
    }

    @DeleteMapping
    public PushSubscriptionResponse unsubscribe(@AuthenticationPrincipal Integer userId,
                                                @RequestBody PushSubscriptionDeleteRequest request) {
        return subscriptionService.unsubscribe(userId, request);
    }

    @PostMapping("/verify")
    public PushSubscriptionVerifyResponse verifySubscription(@AuthenticationPrincipal Integer userId,
                                                             @RequestBody PushSubscriptionVerifyRequest request) {
        return subscriptionService.verifySubscription(userId, request);
    }
}
