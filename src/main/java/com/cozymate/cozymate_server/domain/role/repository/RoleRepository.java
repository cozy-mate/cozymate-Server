package com.cozymate.cozymate_server.domain.role.repository;

import com.cozymate.cozymate_server.domain.role.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByRoomId(Long roomId);

    List<Role> findAllByMateId(Long mateId);

    int countAllByRoomId(Long roomId);

    @Modifying
    @Query("DELETE FROM Role r WHERE r.room.id  = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);
}
