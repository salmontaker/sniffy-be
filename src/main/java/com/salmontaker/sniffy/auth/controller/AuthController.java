package com.salmontaker.sniffy.auth.controller;

import com.salmontaker.sniffy.auth.dto.request.LoginRequest;
import com.salmontaker.sniffy.auth.dto.response.LoginResponse;
import com.salmontaker.sniffy.auth.service.AuthService;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public UserResponse logout(@AuthenticationPrincipal Integer userId) {
        return userService.getUser(userId);
    }
}
