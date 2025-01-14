package com.cozymate.cozymate_server.domain.room;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomHashtag> roomHashtags;

    // 화면상에서 베스트 룸메이트를 연관 시킬 필요가 없어보여서 이름만 저장하도록 했습니다.
    private String bestMateName;

    private int numOfArrival = 1;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "room")
    private Feed feed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    private University university;

    private String dormitoryName;

    public void arrive() {
        numOfArrival++;
    }

    public void quit() {
        numOfArrival--;
    }

    public void isRoomFull() {
        if (numOfArrival == maxMateNum) {
            enableRoom();
        }
    }

    public void updateRoom(String newRoomName, Integer newProfileImage) {
        this.name = newRoomName;
        this.profileImage = newProfileImage;
    }

    public void changeToPublicRoom() {
        this.roomType = RoomType.PUBLIC;
    }

    public void changeToPrivateRoom() {
        this.roomType = RoomType.PRIVATE;
        if (this.getStatus() != RoomStatus.ENABLE) {
            enableRoom();
        }
    }

    public void enableRoom() {
        this.status = RoomStatus.ENABLE;
        this.enabledAt = LocalDate.now();
    }
}
