package com.salmontaker.sniffy.user.service;

import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.dto.request.UserCreateRequest;
import com.salmontaker.sniffy.user.dto.request.UserUpdateRequest;
import com.salmontaker.sniffy.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public User registerUser(UserCreateRequest request) {
        User user = User.create(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getNickname());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("Email already exists");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User changeUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.update(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getNickname());

        return user;
    }

    @Transactional
    public User withdrawUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.delete();

        return user;
    }
}
