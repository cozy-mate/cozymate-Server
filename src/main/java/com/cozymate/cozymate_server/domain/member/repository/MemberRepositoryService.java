package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberRepositoryService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getMemberByIdOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberByIdOptional(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Transactional(readOnly = true)
    public Boolean getExistenceByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public Boolean getExistenceByClientId(String clientId) {
        return memberRepository.existsByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public Member getMemberByClientIdOrThrow(String clientId) {
        return memberRepository.findByClientId(clientId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberByClientIdOptional(String clientId) {
        return memberRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public List<Member> getMemberListByKeywordAndCriteria(
        String keyword, Long universityId, Gender gender, Long viewerId) {
        return memberRepository.findMembersWithMatchingCriteria(
            keyword, universityId, gender, viewerId);
    }

    @Transactional
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public void updateMember(Member member){
        memberRepository.save(member);
    }

}
