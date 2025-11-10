package com.salmontaker.sniffy.notice.repository;

import com.salmontaker.sniffy.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Page<Notice> findByUserId(Integer userId, Pageable pageable);
}
