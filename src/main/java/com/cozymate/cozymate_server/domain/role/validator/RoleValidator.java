package com.cozymate.cozymate_server.domain.role.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidator {

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

}
