package com.cozymate.cozymate_server.domain.member;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private University university;

    private String clientId;

    private String name;

    private String nickname;

    private Gender gender;

    private LocalDate birthDay;

    // 캐릭터 이름이 삭제된다는 기획상의 수정으로 프론트에서 정수로 받고,
    // s3에서 /persona/1 과 같은 식으로 가져오는게 좋아보입니다.
    private Integer persona;
}
