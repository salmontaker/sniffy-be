package com.salmontaker.sniffy.notice.repository;

import com.salmontaker.sniffy.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Page<Notice> findByUserId(Integer userId, Pageable pageable);

    List<Notice> findAllByCreatedAtAfterAndSentAtIsNull(LocalDateTime startOfToday);
}
