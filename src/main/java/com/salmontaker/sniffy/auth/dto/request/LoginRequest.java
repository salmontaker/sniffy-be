package com.salmontaker.sniffy.auth.dto.request;

import com.salmontaker.sniffy.user.validation.Password;
import com.salmontaker.sniffy.user.validation.Username;
import lombok.Data;

@Data
public class LoginRequest {
    @Username
    private String username;

    @Password
    private String password;
}
