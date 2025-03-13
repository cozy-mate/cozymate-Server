package com.cozymate.cozymate_server.domain.inquiry.validator;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InquiryValidator {

    public void checkEmailFormat(String email) {
        boolean emailValid = EmailValidator.getInstance().isValid(email);
        if (!emailValid) {
            throw new GeneralException(ErrorStatus._INQUIRY_EMAIL_FORMAT_INVALID);
        }
    }
}
