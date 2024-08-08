package com.cozymate.cozymate_server.domain.role.repository;

import com.cozymate.cozymate_server.domain.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    void deleteByMateId(Long mateId);

}
