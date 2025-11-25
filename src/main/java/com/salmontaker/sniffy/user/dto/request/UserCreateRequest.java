package com.salmontaker.sniffy.user.dto.request;

import com.salmontaker.sniffy.user.validation.Nickname;
import com.salmontaker.sniffy.user.validation.Password;
import com.salmontaker.sniffy.user.validation.Username;
import lombok.Data;

@Data
public class UserCreateRequest {
    @Username
    private String username;

    @Password
    private String password;

    @Nickname
    private String nickname;
}
