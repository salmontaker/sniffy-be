package com.salmontaker.sniffy.user.service;

import com.salmontaker.sniffy.push.repository.PushSubscriptionRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.dto.request.UserCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse registerUser(UserCreateRequest request) {
        // 아이디 중복여부 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("중복된 아이디가 있습니다.");
        }

        User user = User.create(request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse changeUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        // 비밀번호 업데이트 안하는 경우에 null 들어가면 Encoder에서 Exception 발생
        String password = StringUtils.hasText(request.getPassword()) ?
                passwordEncoder.encode(request.getPassword()) : null;

        user.update(request.getNickname(), password);

        return UserResponse.from(user);
    }

    @Transactional
    public void withdrawUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        pushSubscriptionRepository.deleteByUserId(user.getId());

        user.softDelete();
    }
}
