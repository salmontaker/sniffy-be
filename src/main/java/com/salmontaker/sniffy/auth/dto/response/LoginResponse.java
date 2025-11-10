package com.salmontaker.sniffy.auth.dto.response;

import com.salmontaker.sniffy.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    UserResponse user;
    String token;
}
