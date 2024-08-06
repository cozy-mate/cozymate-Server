package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;

public class TimeUtil {

    private static final String MORNING = "오전";
    private static final String AFTERNOON = "오후";
    private static final Integer HALFDAY = 12;

    public static Integer convertTime(String meridian, Integer time){
        switch (meridian){
            case MORNING:
                if (!time.equals(HALFDAY)){
                    return time;
                }else{
                    return 0;
                }
            case AFTERNOON:
                if(time.equals(HALFDAY)){
                    return time;
                }else {
                    return time + HALFDAY;
                }
            default:
                throw new GeneralException(ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID);
        }
    }

    public static Integer calculateAge(LocalDate birthDate) {
        int currentYear = LocalDate.now().getYear();
        int birthYear = birthDate.getYear();
        return currentYear - birthYear + 1;
    }
}
