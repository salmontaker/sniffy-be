package com.salmontaker.sniffy.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;
}
