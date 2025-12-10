package com.salmontaker.sniffy.push.controller;

import com.salmontaker.sniffy.push.dto.request.PushSubscriptionDeleteRequest;
import com.salmontaker.sniffy.push.dto.request.PushSubscriptionRequest;
import com.salmontaker.sniffy.push.service.PushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/push-subscriptions")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionService subscriptionService;

    @GetMapping
    public boolean subExists(@AuthenticationPrincipal Integer userId,
                             @RequestParam String endpoint) {
        return subscriptionService.subExists(userId, endpoint);
    }

    @PostMapping
    public void subscribe(@AuthenticationPrincipal Integer userId,
                          @RequestBody PushSubscriptionRequest request) {
        subscriptionService.subscribe(userId, request);
    }

    @DeleteMapping
    public void unsubscribe(@AuthenticationPrincipal Integer userId,
                            @RequestBody PushSubscriptionDeleteRequest request) {
        subscriptionService.unsubscribe(userId, request);
    }
}
