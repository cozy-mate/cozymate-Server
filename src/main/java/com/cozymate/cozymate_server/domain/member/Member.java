package com.cozymate.cozymate_server.domain.member;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;

    private String name;

    private String nickname;

    private Gender gender;

    private LocalDate birthDay;

    // 캐릭터 이름이 삭제된다는 기획상의 수정으로 프론트에서 정수로 받고,
    // s3에서 /persona/1 과 같은 식으로 가져오는게 좋아보입니다.
    private Integer persona;

    // 기존에 member -> memberstat 상속 관계를
    // member <-> memberstat one to one mapping으로 변경하였습니다.
    @OneToOne(mappedBy = "member")
    private MemberStat memberStat;

}
