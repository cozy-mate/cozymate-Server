package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import jakarta.annotation.PostConstruct;

@SuppressWarnings("NonAsciiCharacters")
public class RuleFixture {

    public Rule 규칙_1 (Room room) {
        return Rule.builder()
            .room(room)
            .content("20:00-04:00 사이 코어타임 들어오기")
            .memo("안들어오면 델로랑 싸움")
            .build();
    }

    public Rule 규칙_2 (Room room) {
        return Rule.builder()
            .room(room)
            .content("친구를 데려오지말자")
            .memo("데려올거면 미리 허락받기")
            .build();
    }

    public Rule 규칙_3 (Room room) {
        return Rule.builder()
            .room(room)
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo("같이 안먹으면 배고프니까")
            .build();
    }

    public Rule 규칙_4 (Room room) {
        return Rule.builder()
            .room(room)
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo("")
            .build();
    }

    public Rule 메모가_null인_규칙 (Room room) {
        return Rule.builder()
            .room(room)
            .content("배달음식은 모두의 동의를 받고 같이 먹기")
            .memo(null)
            .build();
    }

    public Rule 메모가_너무_많은_규칙 (Room room) {
        return Rule.builder()
            .room(room)
            .content("메모의 최대 길이를 체크해보는 중입니다.")
            .memo("메모의 최대 길이는 50자라고 하네요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요한데, 이렇게 열심히 쓰려고 해야한답니다.") // 72자
            .build();
    }

    public Rule 컨텐츠가_너무_많은_규칙 (Room room) {
        return Rule.builder()
            .room(room)
            .content("컨텐츠의 최대 길이도 50자라고 하는데요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요하고, 지금 저는 그걸 다 끝내고 있죠.") // 73자
            .memo("이제는 컨텐츠의 최대 길이를 체크하고 있습니다.")
            .build();
    }

    public Rule 방이_없는_규칙 () {
        return Rule.builder()
            .room(null)
            .content("컨텐츠의 최대 길이도 50자라고 하는데요. 이걸 제가 다 채우기 위해서는 수많은 노력이 필요하고, 지금 저는 그걸 다 끝내고 있죠.") // 73자
            .memo("이제는 컨텐츠의 최대 길이를 체크하고 있습니다.")
            .build();
    }
}
