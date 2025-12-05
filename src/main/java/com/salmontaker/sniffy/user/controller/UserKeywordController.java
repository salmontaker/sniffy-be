package com.salmontaker.sniffy.user.controller;

import com.salmontaker.sniffy.user.dto.request.UserKeywordCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserKeywordUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserKeywordResponse;
import com.salmontaker.sniffy.user.service.UserKeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/keywords")
@RequiredArgsConstructor
public class UserKeywordController {
    private final UserKeywordService userKeywordService;

    @GetMapping
    public List<UserKeywordResponse> getUserKeywords(@AuthenticationPrincipal Integer userId) {
        return userKeywordService.getUserKeywords(userId);
    }

    @PostMapping
    public UserKeywordResponse createUserKeyword(@AuthenticationPrincipal Integer userId,
                                                 @Valid @RequestBody UserKeywordCreateRequest request) {
        return userKeywordService.createUserKeyword(userId, request);
    }

    @PutMapping("/{id}")
    public UserKeywordResponse updateUserKeyword(@AuthenticationPrincipal Integer userId,
                                                 @PathVariable Integer id,
                                                 @Valid @RequestBody UserKeywordUpdateRequest request) {
        return userKeywordService.updateUserKeyword(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUserKeyword(@AuthenticationPrincipal Integer userId,
                                  @PathVariable Integer id) {
        userKeywordService.deleteUserKeyword(userId, id);
    }
}
