package com.cozymate.cozymate_server.domain.roomhashtag;

import static jakarta.persistence.FetchType.LAZY;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
public class RoomHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    private Room room;

    @ManyToOne(fetch = LAZY)
    private Hashtag hashtag;

}
