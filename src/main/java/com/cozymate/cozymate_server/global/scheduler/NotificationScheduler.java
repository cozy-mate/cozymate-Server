package com.cozymate.cozymate_server.global.scheduler;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class NotificationScheduler {

    private final MateRepository mateRepository;

    private final RoomLogCommandService roomLogCommandService;

    // 매일 자정 반복 (생일인 사람 확인해서 해당 방에 로그 추가)
    @Scheduled(cron = "0 0 0 * * *")
    public void addBirthdayRoomLog() {
        LocalDate today = LocalDate.now();
        List<Mate> mateList = mateRepository.findAllByMemberBirthDayMonthAndDayAndEntryStatus(
            today.getMonthValue(), today.getDayOfMonth(), EntryStatus.JOINED
        );

        mateList.forEach(mate ->
            roomLogCommandService.addRoomLogBirthday(mate, today)
        );
    }


}