package com.cozymate.cozymate_server.domain.mate;

import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Mate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean isExit = false;

    // 룸메이트 대기상태 조회를 위해 EntryStatus를 추가하였습니다.
    @Enumerated(EnumType.STRING)
    private EntryStatus entryStatus = EntryStatus.PENDING;

    private boolean isRoomManager = false;

    private int numOfBest = 0;

    private int numOfVote = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mate bestMate = null;

//    @OneToMany(mappedBy = "mate", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Role> roles;
//
//    @OneToMany(mappedBy = "mate", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Todo> todos;



}
