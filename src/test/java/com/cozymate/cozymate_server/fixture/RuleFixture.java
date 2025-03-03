package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.request.CreateRuleRequestDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class RuleFixture {

    // 정상 더미데이터, content와 memo가 둘 다 존재하는 경우
    public static Rule 정상_1(Room room) {
        return Rule.builder()
            .id(1L)
            .room(room)
            .content("20:00-04:00 사이 코어타임 들어오기")
            .memo("안들어오면 델로랑 싸움")
            .build();
    }

    // 정상 더미데이터, content는 존재하는데, memo가 없는 경우
    public static Rule 정상_4(Room room) {
        return Rule.builder()
            .id(4L)
            .room(room)
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo("")
            .build();
    }

    // 에러 더미데이터, memo가 null인 경우
    public static Rule 값이_null인_memo(Room room) {
        return Rule.builder()
            .id(5L)
            .room(room)
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo(null)
            .build();
    }

    // 에러 더미데이터, memo가 너무 긴 경우
    public static Rule 너무_긴_memo(Room room) {
        return Rule.builder()
            .id(6L)
            .room(room)
            .content("메모의 최대 길이를 체크해보는 중입니다.")
            .memo("메모의 최대 길이는 50자라고 하네요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요한데, 이렇게 열심히 쓰려고 해야한답니다.") // 72자
            .build();
    }

    // 에러 더미데이터, content가 너무 긴 경우
    public static Rule 너무_긴_content(Room room) {
        return Rule.builder()
            .id(7L)
            .room(room)
            .content(
                "컨텐츠의 최대 길이도 50자라고 하는데요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요하고, 지금 저는 그걸 다 끝내고 있죠.") // 73자
            .memo("이제는 컨텐츠의 최대 길이를 체크하고 있습니다.")
            .build();
    }

    // 에러 더미데이터, room이 null인 경우
    public static Rule 값이_null인_room() {
        return Rule.builder()
            .id(8L)
            .room(null)
            .content(
                "컨텐츠의 최대 길이도 50자라고 하는데요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요하고, 지금 저는 그걸 다 끝내고 있죠.") // 73자
            .memo("이제는 컨텐츠의 최대 길이를 체크하고 있습니다.")
            .build();
    }

    public static CreateRuleRequestDTO 정상_1_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("20:00-04:00 사이 코어타임 들어오기")
            .memo("안들어오면 델로랑 싸움")
            .build();
    }

    public static CreateRuleRequestDTO 정상_2_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("친구를 데려오지말자")
            .memo("데려올거면 미리 허락받기")
            .build();
    }

    public static CreateRuleRequestDTO 정상_3_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo("같이 안먹으면 배고프니까")
            .build();
    }

    public static CreateRuleRequestDTO 정상_4_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo("")
            .build();
    }

    public static CreateRuleRequestDTO 값이_null인_memo_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo(null)
            .build();
    }

    public static CreateRuleRequestDTO 너무_긴_memo_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content("메모의 최대 길이를 체크해보는 중입니다.")
            .memo("메모의 최대 길이는 50자라고 하네요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요한데, 이렇게 열심히 쓰려고 해야한답니다.") // 72자
            .build();
    }

    public static CreateRuleRequestDTO 너무_긴_content_생성_요청_DTO() {
        return CreateRuleRequestDTO.builder()
            .content(
                "컨텐츠의 최대 길이도 50자라고 하는데요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요하고, 지금 저는 그걸 다 끝내고 있죠.") // 73자
            .memo("이제는 컨텐츠의 최대 길이를 체크하고 있습니다.")
            .build();
    }

    // 정상 리스트를 반환하는 함수, room은 모두 동일한 Rule 생성
    public static List<Rule> 정상_List(int size, Room room) {
        List<Rule> ruleList = new ArrayList<>();

        IntStream.range(0, size).forEach(i ->
            ruleList.add(Rule.builder()
                .id((long) i + 9) // 기존에 존재한 8개의 rule과 겹치지 않도록 id를 9부터 시작
                .room(room)
                .content("테스트 투두 컨텐츠 " + i)
                .memo("메모 테스트 메모 테스트 " + i)
                .build()
            ));

        return ruleList;
    }
}
