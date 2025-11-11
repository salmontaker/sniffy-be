package com.salmontaker.sniffy.push.dto.response;

import com.salmontaker.sniffy.push.domain.PushSubscription;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushSubscriptionResponse {
    private Integer id;

    public static PushSubscriptionResponse from(PushSubscription subscription) {
        return PushSubscriptionResponse.builder()
                .id(subscription.getId())
                .build();
    }
}
