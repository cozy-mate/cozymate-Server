package com.cozymate.cozymate_server.domain.member.validator;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepositoryService memberRepositoryService;

    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9_]*$";

    @Transactional
    public void checkNickname(String nickname){
        if (memberRepositoryService.getExistenceByNickname(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_EXISTING);
        }

        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new GeneralException(ErrorStatus._INVALID_NICKNAME_PATTERN);
        }

        if (nickname.length() < 2 || nickname.length() > 8) {
            throw new GeneralException(ErrorStatus._INVALID_NICKNAME_LENGTH);
        }
    }

    public void checkClientId(String clientId){
        if (memberRepositoryService.getExistenceByClientId(clientId)) { // 사용자 중복 검증
            throw new GeneralException(ErrorStatus._MEMBER_EXISTING);
        }
    }

    // 나중에 대학검증 할거 많아지면 UniversityValidator 만들어서 옮길게여
    public void checkMajorName(University university, String majorName){
        if(!university.getDepartments().contains(majorName)){
            throw new GeneralException(ErrorStatus._UNIVERSITY_DEPARTMENT_NOT_FOUND);
        }
    }
}
