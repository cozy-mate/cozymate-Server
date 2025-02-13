package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate.LifestyleMatchRateId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LifestyleMatchRateRepository extends
    JpaRepository<LifestyleMatchRate, LifestyleMatchRateId> {

    @Query("SELECT lmr FROM LifestyleMatchRate lmr WHERE lmr.id.memberA = :memberId OR lmr.id.memberB = :memberId")
    List<LifestyleMatchRate> findBySingleMemberId(@Param("memberId") Long memberId);

    @Query("SELECT l FROM LifestyleMatchRate l WHERE l.id IN :idList")
    List<LifestyleMatchRate> findByIdList(@Param("idList") List<LifestyleMatchRateId> idList);

    @Modifying
    @Transactional
    @Query("DELETE FROM LifestyleMatchRate lmr WHERE lmr.id.memberA = :memberId OR lmr.id.memberB = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

}
