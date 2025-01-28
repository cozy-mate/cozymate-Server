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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 12)
    @Pattern(regexp = "^(?!\\s)[가-힣a-zA-Z0-9\\s]+(?<!\\s)$")
    private String name;

    private String inviteCode;

    @NotNull
    @Range(min=2, max=6)
    private int maxMateNum;

    private LocalDate enabledAt;

    // 캐릭터 이름이 삭제된다는 기획상의 수정으로 프론트에서 정수로 받고,
    // s3에서 /persona/1 과 같은 식으로 가져오는게 좋아보입니다.
    @NotNull
    @Range(min=1, max=16)
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "feed_id", referencedColumnName = "id") // 외래 키 관리
    private Feed feed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    private University university;

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

    public void changeToPublicRoom(Gender gender, University university) {
        this.roomType = RoomType.PUBLIC;
        this.gender = gender;
        this.university = university;
    }

    public void changeToPrivateRoom() {
        this.roomType = RoomType.PRIVATE;
        if (this.getStatus() != RoomStatus.ENABLE) {
            enableRoom();
        }
        this.gender = null;
        this.university = null;
    }

    public void enableRoom() {
        this.status = RoomStatus.ENABLE;
        this.enabledAt = LocalDate.now();
    }
}
