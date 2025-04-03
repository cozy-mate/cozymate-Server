package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.Getter;

public class FieldInstanceResolver {

    @Getter
    private static final Map<String, BiFunction<Member, MemberStat, Object>> FIELD_MAPPER = new HashMap<>();

    static {
        // Member 관련 필드 매핑
        FIELD_MAPPER.put("birthYear", (member, memberStat) -> member.getBirthDay().getYear());
        FIELD_MAPPER.put("majorName", (member, memberStat) -> member.getMajorName());
        // MemberStat - MemberUniversityStat
        FIELD_MAPPER.put("admissionYear",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getAdmissionYear());
        FIELD_MAPPER.put("acceptance",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getAcceptance());
        FIELD_MAPPER.put("numOfRoommate",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getNumberOfRoommate());
        FIELD_MAPPER.put("dormitoryName",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getDormitoryName());
        // MemberStat - LifeStyle
        FIELD_MAPPER.put("wakeUpTime",
            (member, memberStat) -> memberStat.getLifestyle().getWakeUpTime());
        FIELD_MAPPER.put("sleepingTime",
            (member, memberStat) -> memberStat.getLifestyle().getSleepingTime());
        FIELD_MAPPER.put("turnOffTime",
            (member, memberStat) -> memberStat.getLifestyle().getTurnOffTime());
        FIELD_MAPPER.put("smoking",
            (member, memberStat) -> memberStat.getLifestyle().getSmokingStatus());
        FIELD_MAPPER.put("sleepingHabit",
            (member, memberStat) -> memberStat.getLifestyle().getSleepingHabit());
        FIELD_MAPPER.put("airConditioningIntensity",
            (member, memberStat) -> memberStat.getLifestyle().getCoolingIntensity());
        FIELD_MAPPER.put("heatingIntensity",
            (member, memberStat) -> memberStat.getLifestyle().getHeatingIntensity());
        FIELD_MAPPER.put("lifePattern",
            (member, memberStat) -> memberStat.getLifestyle().getLifePattern());
        FIELD_MAPPER.put("intimacy",
            (member, memberStat) -> memberStat.getLifestyle().getIntimacy());
        FIELD_MAPPER.put("canShare",
            (member, memberStat) -> memberStat.getLifestyle().getItemSharing());
        FIELD_MAPPER.put("isPlayGame",
            (member, memberStat) -> memberStat.getLifestyle().getPlayingGameFrequency());
        FIELD_MAPPER.put("isPhoneCall",
            (member, memberStat) -> memberStat.getLifestyle().getPhoneCallingFrequency());
        FIELD_MAPPER.put("studying",
            (member, memberStat) -> memberStat.getLifestyle().getStudyingFrequency());
        FIELD_MAPPER.put("intake",
            (member, memberStat) -> memberStat.getLifestyle().getEatingFrequency());
        FIELD_MAPPER.put("cleanSensitivity",
            (member, memberStat) -> memberStat.getLifestyle().getCleannessSensitivity());
        FIELD_MAPPER.put("noiseSensitivity",
            (member, memberStat) -> memberStat.getLifestyle().getNoiseSensitivity());
        FIELD_MAPPER.put("cleaningFrequency",
            (member, memberStat) -> memberStat.getLifestyle().getCleaningFrequency());
        FIELD_MAPPER.put("drinkingFrequency",
            (member, memberStat) -> memberStat.getLifestyle().getDrinkingFrequency());
        FIELD_MAPPER.put("personality",
            (member, memberStat) -> memberStat.getLifestyle().getPersonality());
        FIELD_MAPPER.put("mbti", (member, memberStat) -> memberStat.getLifestyle().getMbti());
    }

    public static Object extractMemberStatField(MemberStat memberStat, String fieldName) {
        BiFunction<Member, MemberStat, Object> fieldGetter = FIELD_MAPPER.get(fieldName);
        if (fieldGetter == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
        }
        return fieldGetter.apply(memberStat.getMember(), memberStat);
    }

    public static Map<String, Object> extractMultiMemberStatFields(MemberStat memberStat,
        List<String> fieldNameList) {
        return fieldNameList.stream()
            .collect(Collectors.toMap(
                fieldName -> fieldName,
                fieldName -> extractMemberStatField(memberStat, fieldName),
                (o1, o2) -> o1,
                LinkedHashMap::new // fieldNameList 조회된 순서 유지
            ));
    }

}
