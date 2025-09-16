package com.salmontaker.sniffy.user.controller;

import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.dto.request.UserCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Integer id) {
        User user = userService.getUser(id);
        return UserResponse.from(user);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.registerUser(request);
        return UserResponse.from(user);
    }

    @PutMapping
    public UserResponse updateUser(
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody UserUpdateRequest request) {
        User user = userService.changeUser(userId, request);
        return UserResponse.from(user);
    }

    @DeleteMapping
    public UserResponse deleteUser(@AuthenticationPrincipal Integer userId) {
        User user = userService.withdrawUser(userId);
        return UserResponse.from(user);
    }
}
