package com.cozymate.cozymate_server.domain.role.repository;

import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleRepositoryService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    public Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));
    }

    public List<Role> getRoleListByRoomId(Long roomId) {
        return roleRepository.findAllByRoomId(roomId);
    }

    public int getRoleCountByRoomId(Long roomId) {
        return roleRepository.countAllByRoomId(roomId);
    }


}
