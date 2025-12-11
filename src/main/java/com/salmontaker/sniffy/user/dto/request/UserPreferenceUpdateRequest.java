package com.salmontaker.sniffy.user.dto.request;

import lombok.Data;

@Data
public class UserPreferenceUpdateRequest {
    private Boolean isPushEnabled;
    private Boolean isFavoriteFirst;
}
