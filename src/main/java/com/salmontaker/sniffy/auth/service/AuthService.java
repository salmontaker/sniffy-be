package com.salmontaker.sniffy.auth.service;

import com.salmontaker.sniffy.auth.domain.RefreshToken;
import com.salmontaker.sniffy.auth.dto.request.LoginRequest;
import com.salmontaker.sniffy.auth.dto.response.AuthResult;
import com.salmontaker.sniffy.auth.repository.RefreshTokenRepository;
import com.salmontaker.sniffy.user.domain.User;
import com.salmontaker.sniffy.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Qualifier("refreshTokenDecoder")
    private final JwtDecoder refreshTokenDecoder;

    public static final long ACCESS_TOKEN_MAX_AGE = Duration.ofMinutes(30).getSeconds();
    public static final long REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7).getSeconds();

    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_MAX_AGE);

        refreshTokenRepository.save(RefreshToken.create(user, refreshToken, expiresAt));

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(Integer userId, String refreshToken) {
        Jwt jwt = validateToken(refreshToken);
        Integer tokenOwnerId = Integer.parseInt(jwt.getSubject());

        if (!userId.equals(tokenOwnerId)) {
            throw new AccessDeniedException("토큰 소유자가 아닙니다.");
        }

        refreshTokenRepository.deleteByUserIdAndToken(tokenOwnerId, refreshToken);
    }

    @Transactional
    public AuthResult refresh(String refreshToken) {
        Jwt jwt = validateToken(refreshToken);
        Integer tokenOwnerId = Integer.parseInt(jwt.getSubject());

        RefreshToken storedToken = refreshTokenRepository.findByUserIdAndToken(tokenOwnerId, refreshToken)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않거나 만료된 토큰입니다."));

        User user = userRepository.findById(tokenOwnerId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String accessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_MAX_AGE);

        storedToken.update(user, newRefreshToken, expiresAt);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private String generateAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_MAX_AGE, "access");
    }

    private String generateRefreshToken(User user) {
        return createToken(user, REFRESH_TOKEN_MAX_AGE, "refresh");
    }

    private String createToken(User user, long expireSeconds, String tokenType) {
        Instant now = Instant.now();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .claim("type", tokenType)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expireSeconds))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private Jwt validateToken(String token) {
        try {
            return refreshTokenDecoder.decode(token);
        } catch (JwtException e) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }
    }
}
