package com.salmontaker.sniffy.push.dto.response;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PushSubscriptionVerifyResponse {
    private LocalDateTime createdAt;

    public static PushSubscriptionVerifyResponse from(PushSubscription pushSubscription) {
        return PushSubscriptionVerifyResponse.builder()
                .createdAt(pushSubscription.getCreatedAt())
                .build();
    }
}
