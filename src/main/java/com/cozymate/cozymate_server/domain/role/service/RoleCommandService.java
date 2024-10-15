package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.RoleRequestDto.CreateRoleRequestDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.CreateRoleResponseDto;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleCommandService {

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;

    /**
     * Role 생성
     * @param member     사용자
     * @param roomId     방 Id
     * @param requestDto Role 생성 요청 DTO
     */
    // TODO: 여러명 선택되었을 때 고려해야됨
    public void createRole(
        Member member, Long roomId, CreateRoleRequestDto requestDto
    ) {
        validateRolePermission(member, roomId);

        int repeatDayBitmast = getDayBitmask(requestDto.getRepeatDayList());

        // TODO: 투두에 추가하지 않으면 해당 부분 삭제
        // 오늘 요일에 해당하는 비트마스크 가져오기
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int dayBitmask = DayListBitmask.getBitmaskByDayOfWeek(dayOfWeek);

        // Role의 대상이 되는 사람
        requestDto.getMateIdList().forEach(mateId -> {
            Mate targerMate = mateRepository.findById(
                    mateId) // TODO: mate를 한번에 가져오는 방식으로 변경해야함
                .orElseThrow(
                    () -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
            Role role = RoleConverter.toEntity(targerMate, requestDto.getTitle(),
                repeatDayBitmast);
            if ((role.getRepeatDays() & dayBitmask) != 0) {
                todoRepository.save(
                    TodoConverter.toEntity(role.getMate().getRoom(),
                        role.getMate(),
                        role.getContent(),
                        LocalDate.now(), role)
                );
            }
            roleRepository.save(role);
        });
    }

    /**
     * Role 삭제
     * @param member     사용자
     * @param roomId     방 Id
     * @param roleId     Role Id
     */
    // TODO: 마찬가지
    public void deleteRole(Member member, Long roomId, Long roleId) {
        validateRolePermission(member, roomId);

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));

        if (Boolean.FALSE.equals(
            member.getId().equals(role.getMate().getMember().getId())
        )) {
            throw new GeneralException(ErrorStatus._ROLE_MATE_MISMATCH);
        }
        roleRepository.delete(role);
    }

    /**
     * Role에 접근할 권한이 있는지 확인
     *
     * @param member 사용자
     * @param roomId 방 Id
     * @return Mate
     */
    private void validateRolePermission(Member member, Long roomId) {
        mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));
    }

    /**
     * 요일 문자열 리스트를 비트마스크로 변환
     * @param repeatDayStringList 요일 리스트
     * @return 비트마스크 값
     */
    private int getDayBitmask(List<String> repeatDayStringList) {
        List<DayListBitmask> repeatDayEnumList = repeatDayStringList.stream()
            .map(DayListBitmask::valueOf).toList();
        return RoleConverter.convertDayListToBitmask(repeatDayEnumList);
    }
}
