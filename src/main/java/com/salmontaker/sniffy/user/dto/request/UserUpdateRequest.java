package com.salmontaker.sniffy.user.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String password;
    private String nickname;
}
