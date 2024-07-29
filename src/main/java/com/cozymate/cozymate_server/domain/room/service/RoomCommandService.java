package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.converter.MateConverter;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.todo.ToDoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomCommandService {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 8;

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository;
    private final ToDoRepository todoRepository;

    @Transactional
    public void createRoom(RoomCreateRequest request) {
        if (roomRepository.existsByMemberIdAndStatuses(request.getCreatorId(), RoomStatus.ENABLE, RoomStatus.WAITING)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toEntity(request, inviteCode);
        roomRepository.save(room);

        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        // 현재는 테스트 위해 임시로 memberId 사용
        Member creator = memberRepository.findById(request.getCreatorId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Mate mate = MateConverter.toEntity(room, creator, true);
        mateRepository.save(mate);

    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
        // 연관된 Mate, To do 엔티티 삭제
        List<Mate> mates = mateRepository.findByRoomId(id);
        for (Mate mate : mates) {
            todoRepository.deleteByMateId(mate.getId());
        }
        mateRepository.deleteAll(mates);
        roomRepository.delete(room);
    }

    // 초대코드 생성 부분

    private String generateUniqueUppercaseKey() {
        String randomKey;
        do {
            randomKey = generateUppercaseKey();
        } while (!isKeyUnique(randomKey));
        return randomKey;
    }

    private boolean isKeyUnique(String key) {
        return !roomRepository.existsByInviteCode(key);
    }

    private String generateUppercaseKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(UPPERCASE_ALPHABET.length());
            key.append(UPPERCASE_ALPHABET.charAt(index));
        }
        return key.toString();
    }

}
