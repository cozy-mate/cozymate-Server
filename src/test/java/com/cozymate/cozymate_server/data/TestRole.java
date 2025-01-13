package com.cozymate.cozymate_server.data;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;

public class TestRole {

    private static final List<String> CONTENT_LIST = List.of(
        "content1",
        "content2",
        "content3"
    );
    private static final List<Integer> REPEAT_DAYS_LIST = List.of(
        0,
        1,
        2
    );

    private RoomRepository roomRepository;
    private MateRepository mateRepository;
    private RoleRepository roleRepository;


    @PostConstruct
    public void init() {
        Room room = roomRepository.findById(1L).orElseThrow();
        Mate mate = mateRepository.findById(1L).orElseThrow();
        List<Mate> assignedMateList = mateRepository.findAllByIdIn(List.of(1L, 2L, 3L));
        Role role = createTestRole(room, mate, assignedMateList);
        roleRepository.save(role);
    }

    public Role createTestRole(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            // 모든 mate가 동일한 방에 있는지 확인해야하는데?
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(CONTENT_LIST.get(0))
            .repeatDays(REPEAT_DAYS_LIST.get(0))
            .build();
    }
}
