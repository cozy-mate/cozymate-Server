package com.cozymate.cozymate_server.domain.member.validator;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepositoryService memberRepositoryService;

    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9_]*$";

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

    public boolean isValidNickname(String nickname){
        if (memberRepositoryService.getExistenceByNickname(nickname)) {
            return false;
        }

        if (!nickname.matches(NICKNAME_PATTERN)) {
            return false;
        }

        if (nickname.length() < 2 || nickname.length() > 8) {
            return false;
        }

        return true;
    }

    public void checkClientId(String clientId){
        if (memberRepositoryService.getExistenceByClientId(clientId)) { // 사용자 중복 검증
            throw new GeneralException(ErrorStatus._MEMBER_EXISTING);
        }
    }
}
