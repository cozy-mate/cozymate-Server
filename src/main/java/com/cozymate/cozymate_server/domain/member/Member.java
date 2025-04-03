package com.cozymate.cozymate_server.domain.member;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "member", indexes = {
    @Index(name = "idx_university_gender", columnList = "university_id, gender")
})
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

    @ManyToOne(fetch = FetchType.EAGER)
    @NonNull
    @JoinColumn(name = "university_id")
    private University university;

    @NonNull
    private String majorName;

    // 정식회원 정보
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDay;

    @Range(min = 1, max = 16)
    private Integer persona;


    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberStat memberStat;

    public void signup(String nickname, Gender gender, LocalDate birthDay, Integer persona) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthDay = birthDay;
        this.persona = persona;
        this.role = Role.USER;
    }

    public void update(String nickname, Integer persona, LocalDate birthDay, String majorName) {
        this.nickname = nickname;
        this.persona = persona;
        this.birthDay = birthDay;
        this.majorName = majorName;
    }

}
