package com.cozymate.cozymate_server.domain.role.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleRepositoryService roleRepositoryService;
    private final MateRepository mateRepository;

    private static final int MAX_ROLE_COUNT = 10;

    /**
     * <p>Role 수정 가능한지 권한을 확인</p>
     * <p>Role의 AssignmentMateIdList에 mate가 있어야 함</p>
     * @throws GeneralException 권한이 없을 경우(ErrorStatus._ROLE_NOT_VALID)
     */
    public void checkUpdatePermission(Role role, Mate mate) {
        if (!role.getAssignedMateIdList().contains(mate.getId())) {
            throw new GeneralException(ErrorStatus._ROLE_NOT_VALID);
        }
    }

    public void checkRoleMaxLimit(Long roomId) {
        int roleCount = roleRepositoryService.getRoleCountByRoomId(roomId);
        if (roleCount >= MAX_ROLE_COUNT) {
            throw new GeneralException(ErrorStatus._ROLE_MAX_LIMIT);
        }
    }

    public void checkMateIdListInSameRoom(List<Long> mateIdList, Long roomId) {
        List<Mate> mateList = mateRepository.findAllById(mateIdList);
        // 모든 mate를 다 가져왔는지 확인
        if (mateList.size() != mateIdList.size()) {
            throw new GeneralException(ErrorStatus._MATE_NOT_FOUND);
        }
        // 모두 같은 방에 있는지 확인
        boolean allInSameRoom = mateList.stream()
            .allMatch(mate -> mate.getRoom().getId().equals(roomId));
        if (!allInSameRoom) {
            throw new GeneralException(ErrorStatus._MATE_NOT_IN_SAME_ROOM);
        }
    }

}
