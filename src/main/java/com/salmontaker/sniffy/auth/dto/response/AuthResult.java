package com.salmontaker.sniffy.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResult {
    private String accessToken;
    private String refreshToken;
}
