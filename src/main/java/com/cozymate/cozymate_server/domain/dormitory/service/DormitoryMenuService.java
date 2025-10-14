package com.cozymate.cozymate_server.domain.dormitory.service;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryMenu;
import com.cozymate.cozymate_server.domain.dormitory.converter.DormitoryMenuConverter;
import com.cozymate.cozymate_server.domain.dormitory.dto.response.DormitoryMenuResponseDTO;
import com.cozymate.cozymate_server.domain.dormitory.repository.DormitoryMenuRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DormitoryMenuService {

    private final DormitoryMenuRepositoryService dormitoryMenuRepositoryService;

    public DormitoryMenuResponseDTO getMenuByDate(Member member, LocalDate date) {
        DormitoryMenu menu = dormitoryMenuRepositoryService.getDormitoryMenuByDateOrThrow(date);

        return DormitoryMenuConverter.toDormitoryMenuResponseDTO(menu, date.getDayOfWeek());
    }
}
