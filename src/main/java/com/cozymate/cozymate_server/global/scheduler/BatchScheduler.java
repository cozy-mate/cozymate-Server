package com.cozymate.cozymate_server.global.scheduler;

import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.service.LifestyleMatchRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class BatchScheduler {

    LifestyleMatchRateService lifestyleMatchRateService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void runLifestyleMatchRateBatch() {
        log.info("라이프 스타일 배치 작업 시작!");
        lifestyleMatchRateService.calculateAllLifeStyleMatchRate();
        log.info("라이프 스타일 배치 작업 종료!");
    }

}
