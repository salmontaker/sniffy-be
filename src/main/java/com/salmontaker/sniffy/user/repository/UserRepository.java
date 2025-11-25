package com.salmontaker.sniffy.user.repository;

import com.salmontaker.sniffy.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
