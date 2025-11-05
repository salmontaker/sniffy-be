package com.salmontaker.sniffy.user.repository;

import com.salmontaker.sniffy.user.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Integer> {
    List<UserKeyword> findAllByUserId(Integer userId);

    Integer countByUserId(Integer userId);
}
