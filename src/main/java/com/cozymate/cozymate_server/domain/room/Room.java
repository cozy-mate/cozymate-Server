package com.cozymate.cozymate_server.domain.room;

import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
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
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String inviteCode;

    private int maxMateNum = 1;

    private LocalDate enabledAt;

    // 캐릭터 이름이 삭제된다는 기획상의 수정으로 프론트에서 정수로 받고,
    // s3에서 /persona/1 과 같은 식으로 가져오는게 좋아보입니다.
    private Integer profileImage;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.WAITING;

    // 화면상에서 베스트 룸메이트를 연관 시킬 필요가 없어보여서 이름만 저장하도록 했습니다.
    private String bestMateName;

    private int numOfArrival = 1;

    public void arrive() {
        numOfArrival++;
    }

    public void isRoomFull() {
        if (numOfArrival == maxMateNum) {
            this.status = RoomStatus.ENABLE;
            this.enabledAt = LocalDate.now();
        }
    }

}

