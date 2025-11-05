package com.salmontaker.sniffy.user.service;

import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.domain.UserKeyword;
import com.salmontaker.sniffy.user.dto.request.UserKeywordCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserKeywordUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserKeywordResponse;
import com.salmontaker.sniffy.user.repository.UserKeywordRepository;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserKeywordService {
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    public List<UserKeywordResponse> getUserKeywords(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        List<UserKeyword> keywords = userKeywordRepository.findAllByUserId(user.getId());

        return keywords.stream().map(UserKeywordResponse::from).toList();
    }

    @Transactional
    public UserKeywordResponse createUserKeyword(Integer userId, UserKeywordCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Integer keywordCount = userKeywordRepository.countByUserId(user.getId());

        if (keywordCount >= 5) {
            throw new IllegalStateException("User can have maximum 5 keywords");
        }

        UserKeyword keyword = UserKeyword.create(user, request.getKeyword());

        return UserKeywordResponse.from(userKeywordRepository.save(keyword));
    }

    @Transactional
    public UserKeywordResponse updateUserKeyword(Integer userId, Integer id, UserKeywordUpdateRequest request) {
        UserKeyword keyword = userKeywordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Keyword not found"));

        Integer keywordOwnerId = keyword.getUser().getId();

        if (!userId.equals(keywordOwnerId)) {
            throw new AccessDeniedException("User id does not match");
        }

        keyword.update(request.getKeyword());

        return UserKeywordResponse.from(keyword);
    }

    @Transactional
    public UserKeywordResponse deleteUserKeyword(Integer userId, Integer id) {
        UserKeyword keyword = userKeywordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Keyword not found"));

        Integer keywordOwnerId = keyword.getUser().getId();

        if (!userId.equals(keywordOwnerId)) {
            throw new AccessDeniedException("User id does not match");
        }

        userKeywordRepository.delete(keyword);

        return UserKeywordResponse.from(keyword);
    }
}
