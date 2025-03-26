package com.cozymate.cozymate_server.domain.university.validator;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

public class UniversityValidator{
    public void checkMajorName(University university, String majorName){
        if(!university.getDepartments().contains(majorName)){
            throw new GeneralException(ErrorStatus._UNIVERSITY_DEPARTMENT_NOT_FOUND);
        }
    }
}
