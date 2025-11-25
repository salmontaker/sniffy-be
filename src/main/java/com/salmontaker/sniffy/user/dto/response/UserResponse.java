package com.salmontaker.sniffy.user.dto.response;

import com.salmontaker.sniffy.user.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String nickname;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}
