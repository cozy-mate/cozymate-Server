package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldInstanceResolver {

    @Getter
    private static final Map<String, BiFunction<Member, MemberStat, Object>> FIELD_MAPPER = new HashMap<>();

    @Getter
    private static final Map<String, Function<Lifestyle, Object>> LIFESTYLE_MAPPER = new LinkedHashMap<>();

    static {
        // Member 관련 필드 매핑
        FIELD_MAPPER.put("birthYear", (member, memberStat) -> member.getBirthDay().getYear());
        FIELD_MAPPER.put("majorName", (member, memberStat) -> member.getMajorName());
        // MemberStat - MemberUniversityStat
        FIELD_MAPPER.put("admissionYear",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getAdmissionYear());
        FIELD_MAPPER.put("dormJoiningStatus",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getAcceptance());
        FIELD_MAPPER.put("numOfRoommate",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getNumberOfRoommate());
        FIELD_MAPPER.put("dormName",
            (member, memberStat) -> memberStat.getMemberUniversityStat().getDormitoryName());
        // MemberStat - LifeStyle
        FIELD_MAPPER.put("wakeUpTime",
            (member, memberStat) -> memberStat.getLifestyle().getWakeUpTime());
        FIELD_MAPPER.put("sleepingTime",
            (member, memberStat) -> memberStat.getLifestyle().getSleepingTime());
        FIELD_MAPPER.put("turnOffTime",
            (member, memberStat) -> memberStat.getLifestyle().getTurnOffTime());
        FIELD_MAPPER.put("smokingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getSmokingStatus());
        FIELD_MAPPER.put("sleepingHabits",
            (member, memberStat) -> memberStat.getLifestyle().getSleepingHabit());
        FIELD_MAPPER.put("coolingIntensity",
            (member, memberStat) -> memberStat.getLifestyle().getCoolingIntensity());
        FIELD_MAPPER.put("heatingIntensity",
            (member, memberStat) -> memberStat.getLifestyle().getHeatingIntensity());
        FIELD_MAPPER.put("lifePattern",
            (member, memberStat) -> memberStat.getLifestyle().getLifePattern());
        FIELD_MAPPER.put("intimacy",
            (member, memberStat) -> memberStat.getLifestyle().getIntimacy());
        FIELD_MAPPER.put("sharingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getItemSharing());
        FIELD_MAPPER.put("gamingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getPlayingGameFrequency());
        FIELD_MAPPER.put("callingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getPhoneCallingFrequency());
        FIELD_MAPPER.put("studyingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getStudyingFrequency());
        FIELD_MAPPER.put("eatingStatus",
            (member, memberStat) -> memberStat.getLifestyle().getEatingFrequency());
        FIELD_MAPPER.put("cleannessSensitivity",
            (member, memberStat) -> memberStat.getLifestyle().getCleannessSensitivity());
        FIELD_MAPPER.put("noiseSensitivity",
            (member, memberStat) -> memberStat.getLifestyle().getNoiseSensitivity());
        FIELD_MAPPER.put("cleaningFrequency",
            (member, memberStat) -> memberStat.getLifestyle().getCleaningFrequency());
        FIELD_MAPPER.put("drinkingFrequency",
            (member, memberStat) -> memberStat.getLifestyle().getDrinkingFrequency());
        FIELD_MAPPER.put("personalities",
            (member, memberStat) -> memberStat.getLifestyle().getPersonality());
        FIELD_MAPPER.put("mbti", (member, memberStat) -> memberStat.getLifestyle().getMbti());


        LIFESTYLE_MAPPER.put("wakeUpTime", Lifestyle::getWakeUpTime);
        LIFESTYLE_MAPPER.put("sleepingTime", Lifestyle::getSleepingTime);
        LIFESTYLE_MAPPER.put("turnOffTime", Lifestyle::getTurnOffTime);
        LIFESTYLE_MAPPER.put("smokingStatus", Lifestyle::getSmokingStatus);
        LIFESTYLE_MAPPER.put("sleepingHabits", Lifestyle::getSleepingHabit);
        LIFESTYLE_MAPPER.put("coolingIntensity", Lifestyle::getCoolingIntensity);
        LIFESTYLE_MAPPER.put("heatingIntensity", Lifestyle::getHeatingIntensity);
        LIFESTYLE_MAPPER.put("lifePattern", Lifestyle::getLifePattern);
        LIFESTYLE_MAPPER.put("intimacy", Lifestyle::getIntimacy);
        LIFESTYLE_MAPPER.put("sharingStatus", Lifestyle::getItemSharing);
        LIFESTYLE_MAPPER.put("gamingStatus", Lifestyle::getPlayingGameFrequency);
        LIFESTYLE_MAPPER.put("callingStatus", Lifestyle::getPhoneCallingFrequency);
        LIFESTYLE_MAPPER.put("studyingStatus", Lifestyle::getStudyingFrequency);
        LIFESTYLE_MAPPER.put("eatingStatus", Lifestyle::getEatingFrequency);
        LIFESTYLE_MAPPER.put("cleannessSensitivity", Lifestyle::getCleannessSensitivity);
        LIFESTYLE_MAPPER.put("noiseSensitivity", Lifestyle::getNoiseSensitivity);
        LIFESTYLE_MAPPER.put("cleaningFrequency", Lifestyle::getCleaningFrequency);
        LIFESTYLE_MAPPER.put("drinkingFrequency", Lifestyle::getDrinkingFrequency);
        LIFESTYLE_MAPPER.put("personalities", Lifestyle::getPersonality);
        LIFESTYLE_MAPPER.put("mbti", Lifestyle::getMbti);
    }

    public static Object extractMemberStatField(MemberStat memberStat, String fieldName) {
        BiFunction<Member, MemberStat, Object> fieldGetter = FIELD_MAPPER.get(fieldName);
        if (fieldGetter == null) {
            log.info("field name : {}", fieldName);
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

    public static Map<String, Object> extractAllLifestyleFields(Lifestyle lifestyle) {
        if (lifestyle == null) return Collections.emptyMap();
        Map<String, Object> result = new LinkedHashMap<>();
        LIFESTYLE_MAPPER.forEach((key, getter) -> {
            try {
                result.put(key, getter.apply(lifestyle));
            } catch (Exception e) {
                log.warn("Failed to extract lifestyle field {}: {}", key, e.getMessage());
                result.put(key, null);
            }
        });
        return result;
    }



}
