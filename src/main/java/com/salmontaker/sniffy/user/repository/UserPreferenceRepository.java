package com.salmontaker.sniffy.user.repository;

import com.salmontaker.sniffy.user.domain.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    Optional<UserPreference> findByUserId(Integer userId);
}
