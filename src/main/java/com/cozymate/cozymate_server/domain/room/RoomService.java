package com.cozymate.cozymate_server.domain.room;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.MateRepository;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 8;

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository; // 추가


    public RoomService(RoomRepository roomRepository, MateRepository mateRepository, MemberRepository memberRepository) {
        this.roomRepository = roomRepository;
        this.mateRepository = mateRepository;
        this.memberRepository = memberRepository;
    }

    public Room createRoom(RoomCreateRequest request) {
        if (roomRepository.existsByMemberIdAndStatuses(request.getCreatorId(), RoomStatus.ENABLE, RoomStatus.WAITING)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = Room.builder()
            .name(request.getName())
            .profileImage(request.getProfileImage())
            .maxMateNum(request.getMaxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
            .numOfArrival(1)
            .build();
        roomRepository.save(room);

        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        // 현재는 테스트 위해 임시로 memberId 사용
        Member creator = memberRepository.findById(request.getCreatorId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Mate mate = Mate.builder()
            .room(room)
            .member(creator)
            .isRoomManager(true)
            .entryStatus(EntryStatus.JOINED)
            .build();
        mateRepository.save(mate);

        return room;
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
