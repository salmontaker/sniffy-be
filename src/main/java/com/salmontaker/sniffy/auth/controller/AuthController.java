package com.salmontaker.sniffy.auth.controller;

import com.salmontaker.sniffy.auth.dto.request.LoginRequest;
import com.salmontaker.sniffy.auth.dto.response.AuthResponse;
import com.salmontaker.sniffy.auth.dto.response.AuthResult;
import com.salmontaker.sniffy.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request,
                              HttpServletResponse response) {
        AuthResult result = authService.login(request);

        ResponseCookie refreshCookie = createRefreshCookie(result.getRefreshToken(), AuthService.REFRESH_TOKEN_MAX_AGE);
        ResponseCookie flagCookie = createFlagCookie(AuthService.REFRESH_TOKEN_MAX_AGE);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, flagCookie.toString());

        return AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .build();
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal Integer userid,
                       @CookieValue(value = "refreshToken", required = false) String refreshToken,
                       HttpServletResponse response) {

        if (userid != null && refreshToken != null) {
            authService.logout(userid, refreshToken);
        }

        ResponseCookie refreshCookie = createRefreshCookie("", 0);
        ResponseCookie flagCookie = createFlagCookie(0);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, flagCookie.toString());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                HttpServletResponse response) {
        AuthResult result = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = createRefreshCookie(result.getRefreshToken(), AuthService.REFRESH_TOKEN_MAX_AGE);
        ResponseCookie flagCookie = createFlagCookie(AuthService.REFRESH_TOKEN_MAX_AGE);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, flagCookie.toString());

        return AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .build();
    }

    private ResponseCookie createRefreshCookie(String refreshToken, long maxAge) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
    }

    private ResponseCookie createFlagCookie(long maxAge) {
        return ResponseCookie.from("login", "true")
                .httpOnly(false)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
