package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.response.exception.WebSocketException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRepositoryService {

    private final MemberRepository memberRepository;

    public Member getMemberByIdOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    public Optional<Member> getMemberByIdOptional(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Boolean getExistenceByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public Boolean getExistenceByClientId(String clientId) {
        return memberRepository.existsByClientId(clientId);
    }

    public Member getMemberByClientIdOrThrow(String clientId) {
        return memberRepository.findByClientId(clientId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
    }

    public Optional<Member> getMemberByClientIdOptional(String clientId) {
        return memberRepository.findByClientId(clientId);
    }

    public List<Member> getMemberListByKeywordAndCriteria(
        String keyword, Long universityId, Gender gender, Long viewerId) {
        return memberRepository.findMembersWithMatchingCriteria(
            keyword, universityId, gender, viewerId);
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public void updateMember(Member member){
        memberRepository.save(member);
    }

    public List<Member> getMemberListByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds);
    }

    public Member getMemberByIdOrSocketThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new WebSocketException(ErrorStatus._MEMBER_NOT_FOUND));
    }
}
