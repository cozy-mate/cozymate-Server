package com.cozymate.cozymate_server.domain.memberstatequality;

import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "member_stat_equality",
    indexes = {
        @Index(name = "idx_member_a_id", columnList = "memberAId")
    })
public class MemberStatEquality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberAId;

    private Long memberBId;

    private Integer equality;

    public void updateEquality(Integer equality) {
        this.equality = equality;
    }

}
