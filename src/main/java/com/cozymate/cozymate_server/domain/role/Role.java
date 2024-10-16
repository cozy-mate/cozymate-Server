package com.cozymate.cozymate_server.domain.role;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
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
public class Role extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mate mate;

    private String content;

    private int repeatDays = 0;

    public void updateEntity(String content, int repeatDays) {
        if (content != null) {
            this.content = content;
        }
        if (repeatDays != -1) { // -1이면 null로 입력된 것
            this.repeatDays = repeatDays;
        }
    }
}
