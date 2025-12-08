package com.cozymate.cozymate_server.domain.dormitory.repository;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryMenu;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DormitoryMenuRepository extends JpaRepository<DormitoryMenu, Long> {
    @Query("""
        SELECT d
        FROM DormitoryMenu d
        WHERE :today BETWEEN d.startDate AND d.endDate
        """)
    Optional<DormitoryMenu> findMenuForThisWeek(@Param("today") LocalDate today);
}
