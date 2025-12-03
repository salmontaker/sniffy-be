package com.salmontaker.sniffy.auth.repository;

import com.salmontaker.sniffy.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByUserIdAndToken(Integer tokenOwnerId, String refreshToken);

    void deleteByUserIdAndToken(Integer userId, String token);
}
