package com.salmontaker.sniffy.user.service;

import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.domain.UserPreference;
import com.salmontaker.sniffy.user.dto.request.UserCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserPreferenceUpdateRequest;
import com.salmontaker.sniffy.user.dto.request.UserUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.dto.response.UserWithPreferenceResponse;
import com.salmontaker.sniffy.user.exception.DuplicateUserException;
import com.salmontaker.sniffy.user.exception.UserNotFoundException;
import com.salmontaker.sniffy.user.exception.UserPreferenceNotFoundException;
import com.salmontaker.sniffy.user.repository.UserPreferenceRepository;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPrefRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;

    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse registerUser(UserCreateRequest request) {
        // 아이디 중복여부 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException();
        }

        User user = userRepository.save(
                User.create(
                        request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getNickname()
                )
        );

        UserPreference userPreference = UserPreference.create(user, false, false);
        userPrefRepository.save(userPreference);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse changeUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // 비밀번호 업데이트 안하는 경우에 null 들어가면 Encoder에서 Exception 발생
        String password = StringUtils.hasText(request.getPassword()) ?
                passwordEncoder.encode(request.getPassword()) : null;

        user.update(request.getNickname(), password);

        return UserResponse.from(user);
    }

    @Transactional
    public void withdrawUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        pushSubscriptionRepository.deleteByUserId(user.getId());

        user.softDelete();
    }

    public UserWithPreferenceResponse getCurrentUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        UserPreference userPref = userPrefRepository.findByUserId(user.getId())
                .orElseThrow(UserPreferenceNotFoundException::new);

        return UserWithPreferenceResponse.from(user, userPref);
    }

    @Transactional
    public UserWithPreferenceResponse updatePreference(Integer id, UserPreferenceUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        UserPreference userPref = userPrefRepository.findByUserId(user.getId())
                .orElseThrow(UserPreferenceNotFoundException::new);

        userPref.update(request.getIsPushEnabled(), request.getIsFavoriteFirst());

        return UserWithPreferenceResponse.from(user, userPref);
    }
}
