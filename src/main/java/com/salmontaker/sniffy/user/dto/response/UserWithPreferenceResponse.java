package com.salmontaker.sniffy.user.dto.response;

import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.domain.UserPreference;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWithPreferenceResponse {
    private Integer id;
    private String username;
    private String nickname;
    private Boolean isPushEnabled;
    private Boolean isFavoriteFirst;

    public static UserWithPreferenceResponse from(User user, UserPreference preference) {
        return UserWithPreferenceResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .isPushEnabled(preference.getIsPushEnabled())
                .isFavoriteFirst(preference.getIsFavoriteFirst())
                .build();
    }
}
