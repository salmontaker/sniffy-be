package com.salmontaker.sniffy.user.service;

import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.dto.request.UserCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserUpdateRequest;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse registerUser(UserCreateRequest request) {
        User user = User.create(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getNickname());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse changeUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.update(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getNickname());

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse withdrawUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.delete();

        return UserResponse.from(user);
    }
}
