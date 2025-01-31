package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionAnswerMapper {

    private static Map<String, List<String>> questionAnswerMap;
    private static final String JSON_FILE = "memberstat/question_answer.json";
    private static final String AM = "오전";
    private static final String PM = "오후";

    public static void load() {

        if (questionAnswerMap == null) { // 중복 로딩 방지
            try (InputStream inputStream = QuestionAnswerMapper.class.getClassLoader()
                .getResourceAsStream(JSON_FILE)) {
                if (inputStream == null) {
                    log.error("파일을 찾을 수 없습니다: {}", JSON_FILE);
                    throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_NOT_FOUND);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                questionAnswerMap = objectMapper.readValue(inputStream,
                    new TypeReference<Map<String, List<String>>>() {
                    });
            } catch (IOException e) {
                log.error("JSON 파일 읽기 실패 {}: {}", JSON_FILE, e.getMessage());
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
            }
        }
    }

    public static int getIndex(String key, String value) {
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        int index = options.indexOf(value);
        if (index == -1) {
            log.error("key : {}. value : {}", key, value);
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return index;
    }



//    private static int handleLegacyCase(String key, String value) {
//        if (key.equals("흡연여부")) {
//            if (value.equals("전자담배")) {
//                return 3;
//            }
//        }
//        if (key.equals("물건공유")) {
//            if (value.equals("아무것도 공유하고싶지 않아요")) {
//                return 0;
//            }
//        }
//        if(key.equals("청소빈도")){
//            if (value.equals("이틀에 한 번 해요")){
//                return 3;
//            }
//        }
//        if(key.equals("음주빈도")){
//            if (value.equals("아예 안 마시요")){
//                return 0;
//            }
//        }
//        return -1;
//    }

    public static String mapValue(String key, Integer index) {
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return Optional.ofNullable(options.get(index))
            .filter(value -> !value.isEmpty() && !value.isBlank())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR));

    }

    public static List<String> mapValues(String key, Integer bitmaskValue) {
        return getIndicesFromBitMask(bitmaskValue).stream()
            .map(index -> mapValue(key, index))
            .collect(Collectors.toList());
    }

    public static Integer convertTimeToInteger(String meridian, Integer hour) {
        if (meridian.equals(AM)) {
            return hour % 12;
        }
        return (hour % 12) + 12;
    }

    public static String calculateMeridian(Integer hour) {
        if (hour < 12) {
            return AM;
        }
        return PM;
    }

    public static Integer calculateHour(Integer hour) {
        int value = hour % 12;
        if (value == 0) {
            return 12;
        }
        return value;
    }

    public static List<Integer> getIndicesFromBitMask(int bitmask) {
        List<Integer> indices = new ArrayList<>();
        int index = 0;

        while (bitmask > 0) {
            // 비트가 1인지 확인
            if ((bitmask & 1) == 1) {
                indices.add(index);
            }
            bitmask >>= 1;
            index++;
        }

        return indices;
    }

    public static Integer convertBitMaskToInteger(String category, List<String> items) {
        int bitMask = 0;
        for (String item : items) {
            int index = getIndex(category, item);
            bitMask |= (1 << index);
        }
        return bitMask;
    }

    public static Map<String, String> convertToStringMap(Map<String, Object> rawMap) {

        log.info("🔹 [Before Conversion] Raw Map: {}", rawMap); // 변환 전 로그

        return rawMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Integer && questionAnswerMap.containsKey(key)) {
                        return mapValue(key, (Integer) value);
                    }

                    return value.toString(); // Integer가 아니면 그냥 String 변환
                }
            ));
    }
}

