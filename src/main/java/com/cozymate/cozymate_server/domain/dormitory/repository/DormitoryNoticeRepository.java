package com.cozymate.cozymate_server.domain.dormitory.repository;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryNotice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DormitoryNoticeRepository extends JpaRepository<DormitoryNotice, Long> {

    // 최신 중요 공지 3개
    List<DormitoryNotice> findTop3ByOrderByCreatedAtDesc();

    // 전체 공지 최신 순
    Page<DormitoryNotice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
