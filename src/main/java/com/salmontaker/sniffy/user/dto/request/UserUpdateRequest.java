package com.salmontaker.sniffy.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Email
    private String email;
    private String password;
    private String nickname;
}
