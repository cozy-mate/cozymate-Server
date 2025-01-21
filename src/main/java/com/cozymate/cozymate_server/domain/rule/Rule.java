package com.cozymate.cozymate_server.domain.rule;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
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
public class Rule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String content;

    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String memo;

    // 메모가 nullable이라, 수정할 때 그냥 값을 덮어씌우도록 구성
    public void updateEntity(String content, String memo) {
            this.content = content;
            this.memo = memo;
    }
}
