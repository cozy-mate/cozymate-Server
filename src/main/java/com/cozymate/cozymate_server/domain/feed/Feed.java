package com.cozymate.cozymate_server.domain.feed;

import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class Feed extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LAZY 설정해도 room에 대한 프록시 정보를 확인하기 위해 EAGER로 동작함
    @OneToOne(mappedBy = "feed")
    private Room room;

    @Column(length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    public void update(FeedRequestDTO feedRequestDTO) {
        this.name = feedRequestDTO.name();
        this.description = feedRequestDTO.description();
    }


}
