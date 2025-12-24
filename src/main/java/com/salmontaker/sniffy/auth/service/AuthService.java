package com.salmontaker.sniffy.auth.service;

import com.salmontaker.sniffy.auth.dto.request.LoginRequest;
import com.salmontaker.sniffy.auth.exception.InvalidCredentialsException;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.dto.response.UserResponse;
import com.salmontaker.sniffy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user);
    }
}
