package com.cozymate.cozymate_server.domain.member;


import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NonNull
    private String clientId;


    @NonNull
    private String nickname;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NonNull
    private LocalDate birthDay;

    @NonNull
    private Integer persona;

    @ManyToOne()
    @JoinColumn(name = "university_id")
    private University university;

    private String majorName;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    private MemberStat memberStat;

    public void verifyMemberUniversity(University university, String majorName){
        this.role = Role.USER_VERIFIED;
        this.university = university;
        this.majorName = majorName;
    }

    public void updatePersona(Integer persona) {
        this.persona = persona;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updateBirthday(LocalDate birthDay) {
        this.birthDay = birthDay;
    }
    public void updateMajor(String majorName) {
        this.majorName = majorName;
    }
}
