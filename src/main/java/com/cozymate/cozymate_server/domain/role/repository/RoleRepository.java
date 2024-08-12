package com.cozymate.cozymate_server.domain.role.repository;

import com.cozymate.cozymate_server.domain.role.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByRoomId(Long roomId);

    Integer countAllByRoomId(Long roomId);

    void deleteByMateId(Long mateId);

}
