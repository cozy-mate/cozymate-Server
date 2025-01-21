package com.cozymate.cozymate_server.domain.memberstat_v2.util;

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
    private static final String JSON_FILE = "memberstat/question-answer.json";

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
        log.info("key:{}, value:{}", key, value);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        int index = options.indexOf(value);
        log.info("index:{}", index);
        if (index == -1) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return index;
    }

    public static String mapValue(String key, Integer index){
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return Optional.ofNullable(options.get(index))
            .filter(value -> !value.isEmpty() && !value.isBlank())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR));

    }
    public static List<String> mapValues(String key, Integer bitmaskValue){
        return getIndicesFromBitMask(bitmaskValue).stream()
            .map(index -> mapValue(key,index))
            .collect(Collectors.toList());
    }

    public static Integer convertTimeToInteger(String meridian, Integer hour) {
        if (meridian.equals("오전")) {
            return hour % 12;
        }
        return (hour % 12) + 12;
    }

    public static String calculateMeridian(Integer hour) {
        if (hour < 12) {
            return "오전";
        }
        return "오후";
    }

    public static Integer calculateHour(Integer hour) {
        int value = hour % 12;
        if (value == 0) {
            return 12;
        }
        return value;
    }

    public static Integer convertSleepingHabitsToInteger(List<String> habits) {
        int bitMask = 0;
        for (String habit : habits) {
            int index = QuestionAnswerMapper.getIndex("잠버릇", habit);
            bitMask |= (1 << index);
        }
        return bitMask;
    }

    public static Integer convertPersonalityToInteger(List<String> personalities) {
        int bitMask = 0;
        for (String personality : personalities) {
            int index = QuestionAnswerMapper.getIndex("성격", personality);
            bitMask |= (1 << index);
        }
        return bitMask;
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
}

