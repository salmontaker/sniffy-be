package com.salmontaker.sniffy.user.dto.request;

import com.salmontaker.sniffy.user.validation.Nickname;
import com.salmontaker.sniffy.user.validation.Password;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Nickname
    private String nickname;

    @Password
    private String password;
}
