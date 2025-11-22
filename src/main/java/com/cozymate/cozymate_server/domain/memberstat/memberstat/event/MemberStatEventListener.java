package com.cozymate.cozymate_server.domain.memberstat.memberstat.event;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.DeleteCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.SaveCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.UpdateCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service.MemberStatCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatEventListener {

    private final MemberStatCacheService memberStatCacheService;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepositoryService memberStatRepositoryService;

    private static final int MAX_RETRY = 3;
    private static final long INITIAL_BACKOFF_MS = 100L;

    private void runWithRetry(String opName, Runnable r) {
        long backoff = INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                r.run();
                if (attempt > 1) log.warn("[{}] succeeded on retry #{}", opName, attempt);
                return;
            } catch (Exception e) {
                if (attempt == MAX_RETRY) {
                    log.error("[{}] failed after {} attempts. giving up.", opName, MAX_RETRY, e);
                } else {
                    log.warn("[{}] failed on attempt #{}, retry in {} ms...", opName, attempt, backoff, e);
                    try { Thread.sleep(backoff); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("[{}] interrupted during backoff. giving up.", opName, ie);
                        return;
                    }
                    backoff *= 2;
                }
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(MemberStatCreatedEvent e) {
        runWithRetry("cache.saveByAnswers(memberId=" + e.memberId() + ")", () ->
            memberStatCacheService.saveByArgs(
                new SaveCommand(e.universityId(), e.gender(), e.memberId().toString(), e.answers())
            )
        );

        runWithRetry("matchRate.save(memberId=" + e.memberId() + ")", () -> {
            MemberStat ms = memberStatRepositoryService.getMemberStatOrThrow(e.memberId());
            lifestyleMatchRateService.saveLifeStyleMatchRate(ms);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onModified(MemberStatModifiedEvent e) {
        runWithRetry("cache.updateByAnswers(memberId=" + e.memberId() + ")", () ->
            memberStatCacheService.updateByArgs(
                new UpdateCommand(e.universityId(), e.gender(), e.memberId().toString(), e.oldAnswers(), e.newAnswers())
            )
        );

        runWithRetry("matchRate.save(memberId=" + e.memberId() + ")", () -> {
            MemberStat ms = memberStatRepositoryService.getMemberStatOrThrow(e.memberId());
            lifestyleMatchRateService.saveLifeStyleMatchRate(ms);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDelete(MemberStatDeleteEvent e){
        runWithRetry("cache.deleteByMemberId(memberId=" + e.memberId() + ")", () ->
            memberStatCacheService.deleteByArgs(
                new DeleteCommand(e.universityId(),e.gender(), e.memberId().toString(), e.answers())
            )
        );

        runWithRetry("matchRate.save(memberId=" + e.memberId() + ")", () -> {
            lifestyleMatchRateService.deleteAllMatchRateByMemberId(e.memberId());
        });
    }
}
