package com.cozymate.cozymate_server.domain.dormitory.repository;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryMenu;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DormitoryMenuRepositoryService {

    private final DormitoryMenuRepository dormitoryMenuRepository;

    public DormitoryMenu getDormitoryMenuByDateOrThrow(LocalDate date) {
        return dormitoryMenuRepository.findMenuForThisWeek(date)
            .orElseThrow(() -> new GeneralException(ErrorStatus._DORMITORY_MENU_NOT_FOUND));
    }

}
